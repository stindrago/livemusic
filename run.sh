#!/bin/sh

## Dichiarazione
#pkg_list="git jack-tools ant fftw3 qjackctl leiningen alsa-utils" # completo
#pkg_list"git qjackctl alsa-utils leiningen" # parziale
pkg_list="$@" # gli passo dal terminale
check() {
    [ -x "$(which $1)" ]
}


## Inizio
echo "\n\n~~Sabato alle 15:00 c'e' il MakerSpace~~\n\n"

## Installo i pacchetti se non sono installati
for pkg_name in $pkg_list; do
    if ! check $pkg_name; then
        echo "Errore: $pkg_name non installato. Installazione in corso..." >&2

        if check "pacman"; then
            sudo pacman -S --no-confirm $pkg_name
        elif check "apt"; then
            sudo apt install -y $pkg_name
        fi

    fi
done

watch amixer -c 0 set PCM 100% > /dev/null 2>&1 & # Massimo volume, perche' no? *:

## Scarico io programma
if ! [ -f "$FILE" ]; then
    git clone https://gitlab.com/aredots/livemusic.git
fi
cd livemusic

pkill -9 "java" # killo le vecchie istanze di java perche uso <CTRL + C> per terminare il programma
lein run
