Jeu du pendu : Hangman

protocole : 
- port : 12345 (par défaul)

Messages sent to the server

- `START [nbLetter] [languageCode]` : to start a game with a given
  number of letter with a specific language, expect FAIL or CORRECT
- `GUESS [letter]` : to guess a specific letter, expect CORRECT, WRONG,
  LOSE or WIN
- `GUESS [word]` : to guess a whole word, expect CORRECT, WRONG, LOSE or
  WIN
- `EXIT` : when the client is exited, ensure the server exits gracefully
  as well

Messages sent to the client

- Receives START :
    - `FAIL PARAM_ERROR` : sent if no word is found corresponding to the
      number of letter given in arguments
    - `CORRECT [updatedWord]` : sends an updated word. Unknown letters
      are sent as `_`
- Receives GUESS :
    - `FAIL PARAM_ERROR`: incorrect parameters
    - `CORRECT [updatedWord]`: see above
    - `WRONG [updatedWord]` : the letter or the word guessed were not
      used or wrong
    - `LOSE [word]` : the game is lost and the correct word is revealed
    - `WIN [word]` : the game is won by the given user and the correct
      word is revealed
- Receives EXIT : is handled by the server. No message is sent back
- Receives anything else
    - `FAIL UNKNOWN_COMMAND` : the command is not understood by the server


![Sequence.png](files%2FSequence.png)

code : features architecture
- picoCLI
- wordPicking

demo :

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server -p 666 -t 6
```

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client -p 666
```

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
