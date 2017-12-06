# Source Code Update 11/27/17 (Brennan Lefler)
We have 3 classes implemented. The main thread, the sample data structure, and the project workflow. The workflow is where most of the implementation will go. I have added comments and function prototypes for the group to guide us along, but feel free to add or remove functions as needed. 

I also went ahead and implemented a function to normalize the data, which successfully converts all of the data to values between 0..1. The "features" ArrayList holds the attribute names, so if you need to get those header names, call that list. 

Unless absolutely necessary, please do not make any changes to any of the functions that import the data or read the data. Everything works as needed to bring in all of the samples and there corresponding features. I know there will be questions about the code as we move forward, so feel free to ask me about it.

As of right now, the GUI is working with a file chooser to select the data. It will only read .CSV and .txt files, which are the normal file types in this area of study. Feel free to mess around with it and identify any issues you find so we can fix them.

# Code Update 11/30/17 (Brennan Lefler)
I updated assumptions about how data will be loaded, to make it easier on us. The user is required to make sure the class is the final attribute in the samples. The sample list does not read in the class; the class is added to a separate list, that we will compare classification outcomes later on (This gives us the confusion matrix, I think...) There is aother list, which contains the names of the classes. So we can assume their indexes as numerical representations of the class. 

What I mean: (iris data example)
flower1 = 0
flower2 = 1
flower3 = 2

# Code Update 12/4/17 (Brennan Lefler)
Implemented functions that split data with even distribution, 70/30 training/test data. Also implemented Bayes function to get class probabilities. It's starting to get quite convoluted, so I apologize. Training samples and classes are in lists named 'trainingSamples', 'trainingClasses'. Same for test samples and classes. ('testSamples', etc.) We need to start working on the Relief algorithm as well.

# Code Update 12/5/17 (Brennan Lefler)
A few more changes added. Currently attempting to implement function to calculate sample probabilities. Also added more output text for the user to see some basic information regarding the included features.

# Code Update 12/6/17 (Brennan Lefler)
Fixed an issue where numerical class names didn't load properly. Added a popup window to catch any exception, so program won't nasty crash for user. Also made some changes above in the readme, to reflect things that don't apply anymore.

I got the sample probabilities function working, it *seems* to be correct. Trying to assign to a class isn't working right now, but the framework is there for it.
