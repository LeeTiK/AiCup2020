start aicup2020.exe --config configBadMap.json --save-results 1.txt --batch-mode
timeout 3
start java -jar aicup2020V12.jar 127.0.0.1 31002
start java -jar aicup2020V12.jar 127.0.0.1 31003
start java -jar aicup2020V12.jar 127.0.0.1 31004