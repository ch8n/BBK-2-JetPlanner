# Big Brain Kotlin : Application #2 - JetPlanner
Day Planner Application for Android

## ScreenShots
![Image](/assets/img.png)
![Image](/assets/img_1.png)
![Image](/assets/img_2.png)

## Feature
- List Task -> Recycler View
- Create task -> Bottom Sheet Form
- Selecting Start - End time -> TimePicker Dialog
- Modify task -> Long press opens update Bottom sheet
- Delete task -> Swipe Right on Recycler View
- Current task -> timer task using coroutines show latest task
- Expire task | Failed task -> time task using coroutines if user doesn't update task status

## Android APIs Used
- Constraint Layout
- Card View
- Recycler View API - ListAdapter & DiffUtil API
- Swipe Gesture API
- Android Styles and Themes
- Custom Fonts
- Material Bottom Sheet Fragments
- Time Picker Dialog
- Kotlinx Date/time Library
- Jetpack Room DB - Entity, DAO
- Jetpack ViewModels
- Jetpack Hilt Dependency Injection

## Kotlin APIs Used
- Coroutines for Concurrency - scope, context, dispatcher
- Flows for observable states and reactive apis - onEach, collect
- Higher order functions -> Lambdas
- Language primitives
    - if/else
    - variables
    - dataTypes - Primitive and UserDefined
- Data classes
- Sealed classes
- Collection operation - map,filter
- Extension functions
- Scoped operation


## Other Patterns and APIs
- MVVM pattern
- Repository + DataSources Pattern


## :cop: License
Shield: [![CC BY-SA 4.0][cc-by-sa-shield]][cc-by-sa]

This work is licensed under a
[Creative Commons Attribution-ShareAlike 4.0 International License][cc-by-sa].

[![CC BY-SA 4.0][cc-by-sa-image]][cc-by-sa]

[cc-by-sa]: http://creativecommons.org/licenses/by-sa/4.0/
[cc-by-sa-image]: https://licensebuttons.net/l/by-sa/4.0/88x31.png
[cc-by-sa-shield]: https://img.shields.io/badge/License-CC%20BY--SA%204.0-lightgrey.svg


