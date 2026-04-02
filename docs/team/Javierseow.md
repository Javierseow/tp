# Javierseow - Project Portfolio Page

## Overview

FitLogger is a command-line fitness tracking application for hybrid athletes who prefer fast keyboard-driven logging of both strength and running workouts.

My main focus was building the project skeleton, implementing the core list to store all the workouts, the run workout workflow, and developing the profile system that lets users customize their profile.

## Summary of Contributions

### Code Contributed

- [Code Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=Javierseow&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enhancements Implemented

- Set up the initial project skeleton, including the `Parser`, `Ui`, and main application entry point, establishing the command-parse-execute architecture used throughout the project.
- Implemented core data models: `RunWorkout`, `WorkoutList`, and the user profile storage class, providing the foundational data structures for the rest of the team to build on.
- Implemented the `add-run` command and `RunWorkout` class, including input validation for distance and duration and flag order enforcement (`d/` before `t/`).
- Implemented `view-total-mileage` (`ViewShoeMileageCommand`), aggregating distance across all logged run workouts.
- Implemented `history` (`ViewHistoryCommand`), displaying all logged workouts in order with numbered indices.
- Implemented `profile view` and `profile set` (`ViewProfileCommand`, `UpdateProfileCommand`), allowing users to store and update their name, height, and weight with bounds validation.

### Contributions to User Guide

- Wrote sections for:
    - `add-run`
    - `profile view`
    - `profile set`
    - `view-total-mileage`
    - `history`

### Contributions to Developer Guide

- Wrote design and implementation documentation for:
  - `add-run` command and `RunWorkout` class (Enhancement 4)
  - `view-total-mileage` command (Enhancement 5)
  - `profile view` and `profile set` commands (Enhancement 6)
- Contributed UML diagrams: `ProfileCommandDiagram`.

### Contributions to Team-Based Tasks

- Established the initial project structure and architecture that the rest of the team built on top of.
- [Pull Requests authored](https://github.com/AY2526S2-CS2113-F09-1/tp/pulls?q=is%3Apr+author%3Adinvsh)