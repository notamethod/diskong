
Consumer Key	FFtWwazbcskFPzduEFOK
Consumer Secret	GOfYoDjZVsACWagKncvDJcjToXwoILJf
Request Token URL	http://api.discogs.com/oauth/request_token
Authorize URL	http://www.discogs.com/oauth/authorize
Access Token URL	http://api.discogs.com/oauth/access_token

//pin NvYUcryoFw

OtaZUJvZoStvbCaHrIjaWgXOLxIZABcUVaNYJkfp

TODO:
PArcours des r�eprtoires finaux
recherche infos par contenu:
R�cup�ration des pistes flac
recherche tags:
artiste: m�me artist ?
oui: m�me album ?
oui: recherche master[0]
nb pistes correspond ?
oui: titres similaires (ignore case) -->%
oui: recherche tags maquants, �crire fichier nfo dans r�pertoire



   AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
    AudioFormat format = audioInputStream.getFormat();
    long audioFileLength = file.length();
    int frameSize = format.getFrameSize();
    float frameRate = format.getFrameRate();
    float durationInSeconds = (audioFileLength / (frameSize * frameRate));
java file audio
shareedit
edited Jun 15 '10 at 15:32
asked Jun 9 '10 at 21:00

Tom Brito
9,44447130225
Which jar file are you using for this? – Umang Kothari Dec 5 '14 at 12:46
add a comment
1 Answer
active oldest votes
up vote
17
down vote
accepted
Given a File you can write

File file = ...;
AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
AudioFormat format = audioInputStream.getFormat();
long frames = audioInputStream.getFrameLength();
double durationInSeconds = (frames+0.0) / format.getFrameRate();
shareedit