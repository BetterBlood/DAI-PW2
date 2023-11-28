Jeu du pendu : Hangman

après analyses règles : EOT et types de messages faisant partie du protocole ->

protocole : 
- port : 12345
- EOT

code : features architecture
- picoCLI : Hangman -> Server/Client chacun run comme subCommand depuis le main
- wordPicking : PoC pour d'autres langages
- vérifications commandes client-server side pour éviter spoofing

demo :
```
start 10 fr
guess e
guess i
guess z
guess k
asd [incorrect command]
guess [word]
start
exit
```
