(ns ninixexe.core
  (:use [overtone.live])
  (:gen-class))

(defn n [n] (midi->hz (note n)))

(demo (sin-osc 80))
(demo
 (let [env (env-gen (perc 0 0.6))]
   (* (sin-osc 80) env)))
(demo
 (let [env (env-gen (perc 0 0.6))]
   (* 2 (sin-osc (+ 40 (* env env env 200))) env)))
(demo
 (let [env (env-gen (perc 0 0.6))
       snare (* (pink-noise) env)]
   snare))
(demo
 (let [env (env-gen (perc 0 0.6))
       snare (* (pink-noise) env)
       snare (+ snare (bpf (* 4 snare) 2000))]
   snare))

(definst kick []
  (let [env (env-gen (perc 0 0.6))]
    (* (sin-osc (+ 40 (* env env env 200))) env)))

(definst snare []
  (let [env (env-gen (perc 0 0.6))
        snare (* 1 (pink-noise) env)
        snare (+ snare (bpf (* 4 snare) 2000))]
    snare))

(defonce metro (metronome 120))

(at (metro (+ (metro) 0)) (kick))
(at (metro (+ (metro) 1)) (snare))

(def bar [[0 kick ]
          [0 snare]])


(defn player [tick]
  (let [beat (mod (int tick) (count bar))]
    (doseq [[offset inst] (partition 2 (bar beat))]
      (at (metro (+ tick offset)) (inst))))
  (apply-by (metro (+ tick 1)) #'player (+ tick 1) []))

(definst kick []
  (let [env (env-gen (perc 0 0.6))
        kick (* 3 (sin-osc (+ 40 (* env env env 200))) env)]
    (clip2 kick 0.5)))

(definst wob [speed 3 freq (n :e2)]
  (let [sweep (lin-exp (lf-tri speed) -1 1 40 3000)
        wob (mix (saw (* freq [0.99 1.01])))
        wob (lpf wob sweep)
        wob (+ wob
               (bpf wob 1500 2)
               (* 0.2 (g-verb wob 9 0.7 0.7)))]
    wob))

(def bar
  (let [wobble [[0 #(ctl wob :freq (n :e1) :speed 1)]
                [0 #(ctl wob :speed 2)]
                [0 #(ctl wob :speed 4)]
                [0 #(ctl wob :speed 8)]
                [0 #(ctl wob :freq (n :f#5) :speed 8)]
                [0 #(ctl wob :freq (n :e2) :speed 2)]
                [0 #(ctl wob :speed 4)]
                [0 #(ctl wob :speed 8)]]

        drum [[  0   kick]
              [  0   snare
               2/4   kick]
              [  0   kick
               3/4   kick]
              [  0   snare]]]

    (mapv concat wobble (cycle drum))))



(defn -main
  "Main class."
  [& args]

  (println "LETS START THE PARTY! \n")
  (println "<CTRL + C> to stop")
  ;(stop)
  (player (metro))
  (wob)
)
