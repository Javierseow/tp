# revaria - Project Portfolio Page

---

## Overview

### Project: FitLogger

FitLogger is a CLI-based fitness tracking application designed for hybrid athletes. It allows users to log both strength (lifts) and endurance (runs) workouts using a streamlined command-line interface, featuring filtering capabilities, calendar views, and a centralized exercise database.

---

## Summary of Contributions

### Code contributed

[Code Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=revaria&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enhancements implemented

#### Enhancement 1: Filter workouts by muscle group (`FilterTypeCommand`)

Designed and implemented a feature allowing users to filter their workout history based on one or more muscle groups. This command iterates through logged workouts and identifies those targeting specific muscle groups via the exercise dictionary.

- **`FilterTypeCommand`**: Processes muscle group filter queries with support for flexible input formats (space-separated, comma-separated, and underscore-separated for multi-word groups like "upper_back" and "lower_back").
- **`parseCategories()` method**: Intelligently parses user input to handle multiple filtering styles:
  - Single muscle groups: `filter pecs`
  - Multi-word muscle groups with underscores: `filter upper_back`
  - Multiple groups space-separated: `filter pecs triceps`
  - Multiple groups comma-separated: `filter quads,hamstring,glutes`
  - Mixed formats: `filter pecs, upper_back triceps`
- **Integration with `ExerciseDictionary`**: Works seamlessly with the muscle group tagging system to identify exercises targeting the specified muscle groups.
- **Comprehensive test coverage**: Created `FilterTypeCommandTest` to validate parsing logic and filtering behavior across various input formats.

#### Enhancement 2: ASCII Workout Calendar (`ViewCalendarCommand`)

Implemented a visual calendar view allowing users to see their training consistency at a glance. This provides a quick way to identify workout gaps and visualize training streaks.

- **`ViewCalendarCommand`**: Renders a traditional monthly ASCII grid in the terminal showing dates with logged workouts highlighted.
- **Calendar generation logic**: Integrates with `WorkoutList` to determine which dates have workouts and format them visually.
- **User-friendly date navigation**: Users can specify month and year (`view-calendar <YYYY-MM>`) to browse different training periods.

#### Enhancement 3: Search workouts by date (`SearchDateCommand`)

Implemented `search-date` command to quickly filter workouts done on a specific date, enabling users to review their training for a particular day without scrolling through full history.

- **Date-based filtering**: Parses user input for date in `YYYY-MM-DD` format and retrieves all workouts from that date.
- **Integrated with `WorkoutList`**: Leverages existing workout storage to efficiently search by date.

### Contributions to the User Guide

Wrote the following sections:

- **Filter workouts by muscle group: `filter`** — Explains filtering syntax with examples for single, multiple, and multi-word muscle groups.
- **View workout calendar: `view-calendar`** — Documents ASCII calendar navigation and usage.
- **Search workouts by date: `search-date`** — Provides examples of date-based workout searching.
- **Updated help system documentation** — Documented the `help` command and its output format.

### Contributions to the Developer Guide

Wrote the following sections:

- **Enhancement: Filter workouts by muscle group** — Detailed design of `FilterTypeCommand`, including:
  - Purpose and user value
  - Class-level design and component behavior
  - Parser-level parsing logic with format support
  - Validation and error handling table
  - Design considerations and alternatives discussed

- **Enhancement: ASCII Workout Calendar** — Full design documentation of `ViewCalendarCommand`, covering:
  - Calendar rendering logic
  - Date selection and navigation
  - Integration with `WorkoutList`

- **Enhancement: Search by date** — Documentation of `SearchDateCommand` design and implementation.

- **Bug fixes and improvements** — Updated documentation for filter command parsing to support multi-word muscle groups with underscore notation (e.g., `upper_back`, `lower_back`).

### Contributions to testing

Implemented comprehensive test coverage for all three major features, achieving 100% code path coverage and extensive edge case validation:

#### Test Suite 1: `FilterTypeCommandTest` — 17 tests
Comprehensive testing of the filter command's parsing and execution logic:
- Single and multiple muscle group filtering
- Space-separated, comma-separated, and underscore-separated input formats
- Case-insensitive matching
- Multi-word muscle groups (e.g., `lower_back` used as "lower back")
- Edge cases: blank input, null input, extra spaces, mixed separators
- Non-existent muscle groups
- Filtering with actual exercise lookup via `ExerciseDictionary`

**Key test scenarios:**
- `filterSingleMuscle_quads_returnsSquatOnly`: validates basic filtering
- `filterMultiWordMuscleGroup_lowerBackWithUnderscore_matchesCorrectly`: ensures underscore notation works
- `filterWithMixedUnderscoreAndSpace_multiWordAndSingleWord`: tests combined formats
- `filterWithCommaAndSpace_mixedSeparators_parsesCorrectly`: validates flexible input parsing

#### Test Suite 2: `SearchDateCommandTest` — 9 tests
Thorough validation of date-based workout filtering:
- Exact date matching with single and multiple workouts
- Boundary conditions: January 1st, December 31st
- Workout type filtering: strength-only, run-only, mixed
- Empty results handling
- Different months with same day-of-month (ensures precise date matching)
- Empty workout lists

**Key test scenarios:**
- `execute_matchingDate_printsOnlyMatchingWorkouts`: confirms date filtering accuracy
- `execute_boundaryDateJanuary1st_returnsMatchingWorkouts`: validates year boundary
- `execute_differentMonthSameDay_noMatches`: ensures month is considered in matching

#### Test Suite 3: `ViewCalendarCommandTest` — 13 tests (NEW)
Complete calendar visualization test coverage:
- Single month with various workout distributions
- Months with no workouts
- Multiple workouts on the same day (deduplicated display)
- Calendar boundaries: February in leap years vs non-leap years
- 30-day months (April, June) vs 31-day months (January, March)
- Different years with the same month (ensures year-month matching)
- Empty workout lists
- Mixed workout types (strength and running)

**Key test scenarios:**
- `execute_singleMonthWithWorkouts_showsCalendarWithActiveDays`: basic calendar rendering
- `execute_multipleWorkoutsSameDay_markedOncePerDay`: validates deduplication
- `execute_februaryLeapYear_showsCorrectDays`: leap year edge case
- `execute_differentYearsSameMonth_onlyTargetYearMarked`: year-month specificity

#### Test Statistics
- **Total new tests**: 39
- **Total lines of test code**: 610 lines
- **Coverage**: All major code paths and edge cases in `FilterTypeCommand`, `SearchDateCommand`, and `ViewCalendarCommand`

---

### Contributions to team-based tasks

- [Pull Requests authored](https://github.com/AY2526S2-CS2113-F09-1/tp/pulls?q=is%3Apr+author%3Arevaria)

---
