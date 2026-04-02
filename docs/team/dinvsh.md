# dinvsh - Project Portfolio Page

---

## Overview

### Project: FitLogger

FitLogger is a desktop fitness tracking application for users who prefer a CLI. It allows users to log strength and running workouts, track personal records, and manage a profile — all from the command line.
 
---

## Summary of Contributions

### Code contributed

[Code Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=dinvsh&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enhancements implemented

#### Enhancement 1: Exercise Shortcut System (`ExerciseDictionary`, `AddShortcutCommand`, `ViewDatabaseCommand`)

Designed and implemented a full exercise shortcut system that lets users log workouts using short numeric IDs instead of typing full exercise names each time.

This involved three interconnected parts:

- **`ExerciseDictionary`**: a new class holding two `TreeMap<Integer, String>` maps (one for lifts, one for runs), pre-loaded with default exercises. `TreeMap` was chosen deliberately over `HashMap` so entries always display in ascending ID order.
- **`AddShortcutCommand`**: allows users to define custom shortcuts at runtime via `add-shortcut <lift/run> <ID> <name>`. Includes full validation: type checking, positive integer ID enforcement, and storage delimiter rejection (`|` and `/`).
- **`ViewDatabaseCommand`**: displays the full shortcut database via `view-database`.
- **Shortcut resolution in `add-lift` and `add-run`**: integrated ID-to-name resolution into the parser so that both `add-lift 2 w/80 s/3 r/8` and `add-lift Bench Press w/80 s/3 r/8` are valid inputs, with no special flag required.

The resolution logic is contained entirely within the `Parser`, keeping `StrengthWorkout` and `AddWorkoutCommand` unchanged; a deliberate design decision to isolate the feature to the parsing layer.

#### Enhancement 2: `add-lift` command and `StrengthWorkout` class

Implemented the `add-lift` command and the `StrengthWorkout` data model, giving users a dedicated CLI path for logging strength training sessions.

- **`StrengthWorkout`** extends the abstract `Workout` base class and adds three validated fields: `weight` (double, ≥ 0), `sets` (int, > 0), and `reps` (int, > 0). Validation is enforced at the setter level so invalid state can never exist in a constructed object.
- **`Parser.parseAddLift(...)`** validates format, enforces correct flag ordering (`w/` before `s/` before `r/`), and rejects names containing storage delimiters.
- Integrated with `EditCommand`: lift-specific fields (`weight`, `sets`, `reps`) can be corrected post-log without deleting and re-entering the workout.
- Persisted to `data/fitlogger.txt` in the format `L | description | date | weight | sets | reps`.

---

### Contributions to the User Guide

Wrote the following sections:

- **Logging a strength workout: `add-lift`**
- **Exercise shortcut database: `view-database`**
- **Adding a custom shortcut: `add-shortcut`**

---

### Contributions to the Developer Guide

Wrote the following sections:

- **Enhancement 3: `add-lift` command and `StrengthWorkout`**: class-level design, sequence of events, storage format, validation table, and design considerations.
- **Enhancement 7: Exercise Shortcut System**: full design write-up covering `ExerciseDictionary`, `AddShortcutCommand`, `ViewDatabaseCommand`, and shortcut resolution in `add-lift`. Includes component-level behavior, validation table, and design considerations with alternatives discussed.

**UML diagrams contributed:**

| Diagram | Type | Status |
|---|---|---|
| `AddWorkoutClassDiagram` | Class | Updated — fixed incorrect `execute()` signature (missing `UserProfile`) and marked `toFileFormat()` as `{abstract}` |
| `AddLiftSequenceDiagram` | Sequence | Updated — fixed incorrect `Parser.parse()` argument (`storage` → `dictionary`) and missing `UserProfile` in `execute()` |
| `AddShortcutClassDiagram` | Class | New — shows `ExerciseDictionary` with `AddShortcutCommand` (mutates) and `ViewDatabaseCommand` (reads) |
| `AddShortcutSequenceDiagram` | Sequence | New — shows the `add-shortcut` command pipeline |
| `ShortcutResolutionSequenceDiagram` | Sequence | New — shows shortcut ID resolution inside `Parser.parseAddLift(...)` before `StrengthWorkout` construction |
 
---

### Contributions to team-based tasks

- [Pull Requests authored](https://github.com/AY2526S2-CS2113-F09-1/tp/pulls?q=is%3Apr+author%3Adinvsh)

---

## Contributions to the Developer Guide (Extracts)

### Enhancement: Exercise Shortcut System

#### Purpose and user value

The exercise shortcut system lets users log workouts by typing a short numeric ID instead of the full exercise name every time. For example, `add-lift 2 w/80 s/3 r/8` resolves to `Bench Press` via the database, avoiding repetitive typing.

The system has three parts:
- `ExerciseDictionary`: the in-memory data model storing ID-to-name mappings for lifts and runs.
- `AddShortcutCommand`: lets users extend the database with their own shortcuts at runtime.
- `ViewDatabaseCommand`: lets users see what shortcuts are currently available.

The database ships with four default lift shortcuts and three default run shortcuts. Custom shortcuts added via add-shortcut are permanently persisted to the save file alongside the user's profile and workout history.

#### Class-level design

The class diagram below shows the relationships between the components.

![Add Shortcut Class Diagram](../images/AddShortcutClassDiagram.png)

`ExerciseDictionary` is the shared data model. `AddShortcutCommand` mutates it; `ViewDatabaseCommand` reads from it. Both extend the abstract `Command` base class, keeping them consistent with the rest of FitLogger's command pipeline.

`ExerciseDictionary` uses two `TreeMap<Integer, String>` fields; one for lifts, one for runs. `TreeMap` was chosen over `HashMap` so entries are always displayed in ascending ID order by `ViewDatabaseCommand`.

#### Shortcut resolution in `add-lift`

Using a shortcut ID in `add-lift` (e.g. `add-lift 2 w/80 s/3 r/8`) triggers a resolution step inside `Parser.parseAddLift(...)` before the `StrengthWorkout` is created. The sequence diagram below shows this flow.

![Shortcut Resolution Sequence Diagram](../images/ShortcutResolutionSequenceDiagram.png)

The resolution logic is a try-catch around `Integer.parseInt(name)`. If parsing succeeds, `dictionary.getLiftName(id)` is called. A `null` return means the ID does not exist, and a `FitLoggerException` is thrown pointing the user to `view-database`. If `NumberFormatException` is thrown, the token is treated as a plain-text name. This means shortcut resolution is entirely a parsing concern — `StrengthWorkout` and `AddWorkoutCommand` are unchanged.
