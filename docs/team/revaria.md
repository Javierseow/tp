# revaria - Project Portfolio Page

## Overview

FitLogger is a CLI-based fitness tracking application designed for hybrid athletes. It allows users to log both strength (lifts) and endurance (runs) workouts using a streamlined command-line interface, featuring custom shortcuts and a centralized exercise database.

### Summary of Contributions

#### Code Contributed
I was responsible for the core command-line parsing logic and the user-facing error handling systems. You can view my code contributions [here](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=revaria&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=).

#### Enhancements Implemented
* **Upgraded Command Parser:** I refactored the `Parser` class to support multi-argument commands using a robust splitting logic. This allows users to input multi-word exercise names (e.g., "Romanian Deadlift") without the parser breaking, providing a more natural user experience.
* **Comprehensive Error Handling:** I implemented a custom exception architecture (`FitLoggerException`) that catches invalid inputs—such as non-numeric weights or out-of-bounds metrics—and provides the user with helpful "Usage" hints instead of crashing the application.
* **Integrated Help System:** I designed and implemented the `help` command, which dynamically generates a menu of all available commands, flags (like `w/`, `s/`, `r/`), and example usages to reduce the learning curve for new users.
* **Profile Validation Logic:** I created a validation layer for user profiles that enforces realistic bounds for height (0.3m–3.0m) and weight (10kg–500kg), ensuring data integrity within the app's internal storage.

#### Documentation Contributions
* **User Guide:** Authored sections on Command Parsing Rules and Error Resolution to help users understand how the app interprets their input.
* **Developer Guide:** Documented the `Parser` and `Logic` component interactions, including sequence diagrams for command execution flows.
