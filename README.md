# Emotion Analysis Framework

The Emotion Analysis Framework analyzes the prevalence of five key emotions in text:

- Anger
- Disgust
- Fear
- Joy
- Sadness

## Overview
#### Framework Domain
The framework domain receives documents represented as strings of text from the data plugins,
which it then analyzes. This analysis is comprised of calculations of the the five emotions
in the text and important keywords.

#### Most Important APIs
The most important APIs used in the Emotion Analysis Framework are as follows:
- **IBM Watson API**
    - Used in the framework for emotion and keyword analysis in the text
- **JSoup API**
    - Used in the data plugin to scrape webpage content
- **Twitter API**
    - Used in the data plugin to pull fifty most recent tweets given a Twitter handle
- **News API**
    - Used in the data plugin to pull articles given a search keyword and news source
- **Word Cloud API**
    - Used in the visualization plugin to generate word clouds given framework data
- **XChart API**
    - Used in the visualization plugin to create bar graphs
    
## Instructions
#### How to implement plugins
##### Data Plugins
To write your own data plugin, please see the data plugin interface below:

[Data Plugin Interface](./framework/src/main/java/edu/cmu/cs/cs214/hw5/core/DataPlugin.java)

Then, add your new plugin to [META-INF.services/.../DataPlugin](./plugins/src/main/resources/META-INF/services/edu.cmu.cs.cs214.hw5.core.DataPlugin) like so:
```
edu.cmu.cs.cs214.hw5.core.data.<NEW_PLUGIN_NAME> // Add your new plugin here
```

##### Visualization Plugins
To write your own visualization plugin, please see the visualization plugin interface below:

[Visualization Plugin Interface](./framework/src/main/java/edu/cmu/cs/cs214/hw5/core/VisualPlugin.java)

Then, add your new plugin to [META-INF.services/.../VisualPlugin](./plugins/src/main/resources/META-INF/services/edu.cmu.cs.cs214.hw5.core.VisualPlugin) like so:
```
edu.cmu.cs.cs214.hw5.core.visual.<NEW_PLUGIN_NAME> // Add your new plugin here
```
#### Where to get and store your API keys
When creating new plugins requiring the use of an API and a designated API key, use the following guidelines:
1) Store your API key in the [secret.properties](./framework/src/main/resources/secret.properties) file like this:
```
API_KEY_NAME=yX27EV...zU
```
2) Get your API key like this:
```java
Properties prop = new Properties();
prop.load(new FileInputStream("../framework/src/main/resources/secret.properties"));
String apiKey = prop.getProperty("API_KEY_NAME");
```


