# TaskFlow — Bug-Fixing Exercise

TaskFlow is a task-management REST API built with **Spring Boot 3 / Java 21**.
The codebase contains **53 intentional bugs** spread across controllers, services, mappers, models, and utilities.
Your job is to find and fix the bug described in your assigned GitHub issue, then open a Pull Request.

---

## Prerequisites

| Tool       | Minimum version | Download                                                              |
| ---------- | --------------- | --------------------------------------------------------------------- |
| Java (JDK) | 21              | [Adoptium Temurin 21](https://adoptium.net/temurin/releases/?version=21) |
| Maven      | 3.9             | [maven.apache.org](https://maven.apache.org/download.cgi)                |
| Git        | any recent      | [git-scm.com](https://git-scm.com/downloads)                             |

> **Tip — no Maven installed?**
> The project ships with a Maven Wrapper. Use `./mvnw` (Linux/macOS) or `mvnw.cmd` (Windows) instead of `mvn` in every command below.

---

## Quick Start

### Linux / macOS

```bash
git clone https://github.com/MrAech/taskflow.git
cd taskflow
bash setup.sh        # checks Java/Maven, compiles, runs baseline tests
```

### Windows (Command Prompt)

```bat
git clone https://github.com/MrAech/taskflow.git
cd taskflow
setup.bat
```

After setup you will see Maven output with several **FAILED** tests — that is intentional. Those failures are the bugs waiting to be fixed.

---

## Your Workflow

```
1. Find your assigned issue in the GitHub Issues tab
2. Read  issues/issue-NNN.md  for the full description
3. Fix the bug in  src/main/java/
4. Verify the fix:
     Linux/macOS:  ./check.sh  <issue-number>
     Windows:       check.bat  <issue-number>
5. Commit your change and open a Pull Request
   Branch name must follow the format:  <YourID>_<IssueNumber>
   Example:  alice_15   or   s2024001_42
```

> **Rules**
>
> - Fix **only** the bug described in your issue — don't refactor unrelated code.
> - Do **not** modify any file under `src/test/`.
> - One commit per issue is strongly preferred.
> - Do **not** merge your own PR — the instructor will review and merge.

---

## Checking Your Fix

```bash
# Linux / macOS
./check.sh 15

# Windows
check.bat 15
```

The script runs only the tests that cover issue #15 and prints a **PASS** or **FAIL** banner.

You can also run the full test suite at any time:

```bash
mvn test            # Linux/macOS/Windows (if mvn is on PATH)
mvnw.cmd test       # Windows with wrapper only
```

---

## Tech Stack

| Layer       | Technology                       |
| ----------- | -------------------------------- |
| Framework   | Spring Boot 3.4.3                |
| Language    | Java 21                          |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Security    | Spring Security                  |
| Validation  | Jakarta Bean Validation          |
| Build       | Maven 3                          |
| Testing     | JUnit 5 + Mockito + MockMvc      |

---

## FAQ

**Q: The project won't compile at all — is that normal?**
A: Some bugs cause compilation errors. That is part of the exercise. Fix the bug indicated in your issue and the build should recover.

**Q: Tests unrelated to my issue are also failing — do I need to fix those?**
A: No. Focus only on the test(s) listed in your issue. Other failures belong to other students' issues.

**Q: Can I run the app locally?**
A: Yes — `mvn spring-boot:run`. The app starts on `http://localhost:8080` with an in-memory H2 database seeded from `data.sql`.

**Q: I'm on Windows and `./check.sh` gives an error.**
A: Use `check.bat` instead. If you prefer the shell script, install [Git for Windows](https://git-scm.com/downloads) and run commands inside **Git Bash**.

**Q: My fix passes `check.bat` but I'm not sure it's complete.**
A: Read the *Steps to Reproduce* and *Acceptance Criteria* sections in your issue file for the full expected behaviour.

---

## Submitting Your Pull Request

1. Push your branch to the repository:
   ```bash
   git push origin <YourID>_<IssueNumber>
   ```
2. Go to **GitHub → Pull Requests → New pull request**.
3. Set the base branch to `master` and compare to your branch.
4. Title the PR: `Fix #<IssueNumber> — <short description>`.
5. Reference the issue in the body: `Closes #<IssueNumber>`.
6. Wait for the instructor's review — **do not merge it yourself**.

---

*Happy debugging!*
