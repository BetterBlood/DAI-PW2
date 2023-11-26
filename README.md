# Hangman

## Instructions

Global help
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar help
```

Client help
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client help
```

automatic client
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client
```

client with parameters
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client -p 666
```

Server help
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server help
```

automatic server
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server
```

server with parameters
```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server -p 666 -t 6
```

### Protocol documentation

The protocol uses the end of transmission character as the Hangman game may involve words of various lengths.

Messages sent to the server
- START [nbLetter] [languageCode] : to start a game with a given number of letter with a specific language, expect FAIL or CORRECT
- GUESS [letter] : to guess a specific letter, expect CORRECT, WRONG, LOSE or WIN
- GUESS [word] : to guess a whole word, expect CORRECT, WRONG, LOSE or WIN
- EXIT : when the client is exited, ensure the server exits gracefully as well

Messages sent to the client
- Receives START :
  - FAIL [reason] : sent if no word is found corresponding to the number of letter given by START
  - CORRECT [updatedWord] : sends an updated word. Unknown letters are sent as `_`
- Receives GUESS :
  - FAIL [reason]: incorrect parameters
  - CORRECT [updatedWord]: see above
  - WRONG [updatedWord] : the letter or the word guessed were not used or wrong
  - LOSE [word] : the game is lost and the correct word is revealed
  - WIN [word] : the game is won by the given user and the correct word is revealed
- Receives EXIT : is handled by the server. No message is sent back

### Génération de mots
#### FR
https://raw.githubusercontent.com/chrplr/openlexicon/master/datasets-info/Liste-de-mots-francais-Gutenberg/liste.de.mots.francais.frgut.txt
puis transformation en ASCII avec un programme fait maison

#### EN
https://github.com/dwyl/english-words/blob/master/words_alpha.txt