# Hangman

## Instructions

### Building the application

Either use the "Package as JAR file" run configuration or :

```bash
maven dependency:resolve clean compile package
```

### Run the server and a client

Then, in two or more terminals, one dedicated to the server and the
others to the clients, run the following commands :

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server
```

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client
```

Once the client connected to a server, a list of options to use are
shown.

### All commands available

Global help

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar help
```

Client help

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client help
```

Client with parameters

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar client -p 666
```

Server help

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server help
```

Server with parameters

```bash
java -jar target/dai-pw2-1.0-SNAPSHOT.jar server -p 666 -t 6
```

## Network Application Protocol Documentation

The protocol uses the end of transmission character as the Hangman
game may involve words of various lengths.

The procotol is initiated by the client as seen on the diagram below
for a typical execution for a game.

![Sequence.png](files%2FSequence.png)

Transmissions are verified on each side. In case of any errors a FAIL
message is sent with a matching error code.

Messages sent to the server

- START [nbLetter] [languageCode] : to start a game with a given
  number of letter with a specific language, expect FAIL or CORRECT
- GUESS [letter] : to guess a specific letter, expect CORRECT, WRONG,
  LOSE or WIN
- GUESS [word] : to guess a whole word, expect CORRECT, WRONG, LOSE or
  WIN
- EXIT : when the client is exited, ensure the server exits gracefully
  as well

Messages sent to the client

- Receives START :
  - FAIL PARAM_ERROR : sent if no word is found corresponding to the
    number of letter given in arguments
  - CORRECT [updatedWord] : sends an updated word. Unknown letters
    are sent as `_`
- Receives GUESS :
  - FAIL PARAM_ERROR: incorrect parameters
  - CORRECT [updatedWord]: see above
  - WRONG [updatedWord] : the letter or the word guessed were not
    used or wrong
  - LOSE [word] : the game is lost and the correct word is revealed
  - WIN [word] : the game is won by the given user and the correct
    word is revealed
- Receives EXIT : is handled by the server. No message is sent back
- Receives anything else
  - FAIL UNKNOWN_COMMAND : the command is not understood by the server

## Word generation

We are using txt files containing words in a given language.
The txt files are comprised of alphabetic data and the special hyphen
character.
A dedicated java file ```TxtToAscii``` exists to ensure special
characters are converted into standard alphabet.
As supporting all the possible languages can quickly grow out of scope
for this project, we focused our efforts on french and english, as a
proof of concept.

Example : "préférence" is turned into "preference"

Source for each dictionary used :

- EN : https://github.com/dwyl/english-words/blob/master/words_alpha.txt
- FR : https://raw.githubusercontent.com/chrplr/openlexicon/master/datasets-info/Liste-de-mots-francais-Gutenberg/liste.de.mots.francais.frgut.txt

## Others

The ASCII art for the hanged man comes from https://gist.githubusercontent.com/chrishorton/8510732aa9a80a03c829b09f12e20d9c/raw/b7feb295b3bdb3a5dd92868b627d5bdd46f4cc76/hangmanwordbank.py