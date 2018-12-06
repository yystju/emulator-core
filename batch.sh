#!/bin/bash
for cap in `seq 100 50 250`; do
    for j in `seq 5 5 20`; do
        let 'width = cap / 5'
        let 'avg = cap / 10 - j'
        if [ $avg -gt 0 ]; then
            echo $cap $avg "./out_"$cap"_"$avg".txt"
            ./cleanup.sh
            #java -jar ./target/emulator-core-0.0.1-SNAPSHOT.jar ./data --takt=2000 --avg=$avg --cap=$cap --noshuffle=false --shuffule.ratio=10.0 --coating.takt.moduler=3 --assembly.takt.moduler=4 --window.width=$width > "./out_"$cap"_"$avg".txt" 2>&1
            java -jar ./target/emulator-core-0.0.1-SNAPSHOT.jar ./data --takt=2000 --avg=$avg --cap=$cap --noshuffle=false --coating.takt.moduler=3 --assembly.takt.moduler=4 --window.width=$width > "./out_"$cap"_"$avg".txt" 2>&1
        fi
    done
done