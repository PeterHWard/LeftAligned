# LeftAligned
Write in Word. Deliver in Final Draft.

LeftAligned is a Java GUI app that converts "stage play" formatted screenplays written in Word 2007 (.docx) to Final Draft 8 XML (.fdx).

Write:
```
FADE IN: 

INT. ALICE'S APARTMENT - DAY

Some boilerplate scene description a better screen 
writer would have done without. 

ANGLE ON

Alice.  In the kitchen; stirring a pot on the stove.  
She has her mobile phone jammed to her ear --

ALICE: (to phone) Mom!  Listen.  The garlic has only 
been in a minute.  Mom!  

INSERT 

the frying pan.  The garlic is starting to burn...
```

and get:

```
                                            FADE IN: <Transition>

INT. ALICE'S APARTMENT - DAY <Scene Heading>

Some boilerplate scene description a better screen 
writer would have done without. <Action>

ANGLE ON <Shot>

Alice.  In the kitchen; stirring a pot on the stove.  
She has her mobile phone jammed to her ear --

                  ALICE <Character>
                (to phone) <Parenthetical>
          Mom!  Listen.  The garlic has 
          only been in a minute.  Mom! <Dialogue>

INSERT <Shot>

the frying pan.  The garlic is starting to burn... <Action>
```

## Features
- Write screenplays in Word or other text editor with .docx (Word 2007) export.
- Keep text left-aligned and simply follow some simple formatting rules
- Automatically breaks dialogue paragraphs into their component parts: character name, parentheticals, and dialogue
- Recognition of scene headings, shots, and transitions
- Retains the following styles supported by Final Draft: Italic, Bold, Underline
- Simple GUI interface or can be adapted to CLI or integrated within your own application 
- One-way conversion at the moment

## Installation
From source only at the moment.

## Documentation

### Basic usage
Simply launch the app and select a source file. Optionally provide an alternative name/save-to location. 

Press "Convert".  

### Formatting in Word
While LeftAligned avoids the need for complicated style sheets, it does require that you adhere to a few rules:

**Scene Headings** must be ALL CAPS and use either a `INT.` or `EXT.` prefix or `- DAY`, `- NIGHT`, `- CONTINUOUS` or `- NEXT` suffix. 

**Dialogue** paragraphs must start with an ALL CAPS character name followed by colon: `CHARACTER NAME:`. Any text following the first `:` that is contained within `()` will be treated as a parethetical. The rest as literal dialogue. For his reason, it is important to use alternatives such as `[]` or `{}` for bracketing. Any text within `()` *preceding* the first colon is treated as part of the name itself, following what Final Draft XML does.

**Transitions** must be ALL CAPS and have either `FADE` or `TO:` within them. 

**Shot** is the default for any ALL CAPS paragraph that is not determined to be a Scene Heading or Transition. Scene Headings that are missing a required component will likely be typed Shots.

**Action** is the default for what's left after the above patterns have been tested for. I.e., normal prose. Camera direction or other ALL CAPS strings within an Action paragraph will be treated as part of that paragraph. Keep `ANGLE ON`, `CLOSE UP`, etc. in separate paragraphs if you want them to be type Shots. 

Instead of 

```
CLOSE UP as Bob reaches for the gun.
```

use 

```
CLOSE UP 

as Bob reaches for the gun.
```

**Note:** Final Draft also has a **General** element. While the ScriptDocument API (see below) supports this element, nothing converted by LeftAlign will be set to General as any unidentifiable patterns will default to Action.


### CLI usage/integration with other software
The class LeftAligned can serve as an entry point for your application. It does not implement a `main()` method of it's own. However, you can easily wrap it in one for CLI use.
```java
LeftAligned la = new LeftAligned(); 
la.makeFDX("myScreenPlay.docx");
la.writeFDX("myScreenPlay.fdx");
```

You can also pass a `java.io.File` object to both `.makeFDX(File fo)` and `.writeFDX(File fo)` methods. 

### Libraries of note
Apache POI's XWPFDocument API (org.apache.poi.xwpf) is used to read the `.docx` source file. 

The GUI is JavaFX.

### The ScriptDocument class
LeftAligned does not convert directly from the source document to Final Draft XML. A custom collection of objects are used to encapsulate the document as a whole as well as contain its component parts. This simplifies future bi-directionality as well as enables support for other file formats besides .docx and .fdx.

The hierarchy of ScriptDocument is:

`ScriptDocument -> SceneGroup -> ElementGroup -> ScriptElement -> Text`

#### ScriptDocument
This is the top-level. Equivalent to `XWPFDocument`. Its `members` property contains an ArrayList of SceneGroups

#### SceneGroup
LeftAligned follows the Final Draft convention of treating each Scene Heading as the start of a new scene. Therefore, documents created with LeftAligned will feature one SceneGroup per SceneHeading. (The first SceneGroup may not have a Scene Heading, however.) However, the ScriptDocument itself does not depend on this being the case. Its `members` property can contain any assortment of ElementGroup objects.

See my "Note on Scene Groups" below on a better way a screen writing application could organize scenes.

#### ElementGroup
Final Draft itself only has elements. So Parenthetical and Dialogue elements can be used without an associated Character element, even though dialogue without an associated character name isn't likely to be useful. Therefore, it seemed prudent to group logically bound elements together. With the current implementation, the only ElementGroup that can contain more than one ScriptElement type is DialogueGroup. In future, Scene Headings may be broken into several sub-element types, although Final Draft XML draft treats Scene Headings as a single element.

#### ScriptElement
These objects correspond directly to the element types Final Draft XML uses. The field `type` contains a string with the exact spelling for the type used by the Final Draft `Paragraph` element's `Type` attribute. 

#### Text
Basically the equivalent of a 'run' in MS Office documents, and corresponding to Final Draft's `Text` XML element. Each Text object contains a `textContent` field was well as a `style` field. `style` holds a whitespace-delimited string of all styles that apply. For simplicity, style strings match spelling used by Final Draft Text element's `Style` attribute. Invalid style names are simply ignored (possibly erased?) by Final Draft.

### Metadata support
In the limited testing I've done so far, it appears Final Draft generates metadata, such as location lists and character lists, automatically when you first open the LeftAligned rendered file. If further testing indicates this is not the case, there is a SceneProperties class--currently unused--that can contain this metadata. 

### Note about Unicode
The limited range of fonts supported by Final Draft (TrueType only I believe) can be problematic for writers using languages other than English. For its part, LeftAligned will faithfully pass non-ASCIIs through. What Final Draft does with them is another story. Final Draft can be used for free in "Reader Mode". I would suggest inspecting the converted file to ensure any Unicode rendered as expected. If not, transliteration may be required unfortunately. 

Also, be aware that "smart type" characters generated by Word and other word processors may not be interpreted correctly by Final Draft. At the moment, dashes/hypens are normalized to keyboard defaults in Scene Headings only--this is to aid in proper Scene Heading recognition. I would recommend disabling "smart type" functionality while writing for your screenplays to avoid surprises. 

### Note on Scene Groups
Final Draft naively treats a Scene Heading as the start of the new scene. Often this isn't what you want. What if a character moves from inside to outside? This will require a new Scene Heading, but is arguably part of the same logical scene. 

If I had my way, I would make "Start of Scene" (SOS) a hidden element that can be placed arbitrarily. One hack I've seen is to only use a Scene Heading when a scene starts and Shot elements for the rest of your sluglines. Unfortunately, you lose typeahead when you do this. For now, LeftAligned simply follows Final Draft in the way it defines a "scene".
