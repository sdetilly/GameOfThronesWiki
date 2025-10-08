# Game of Thrones Wiki 🐉

[![Coverage](https://img.shields.io/badge/Coverage-Check%20CI-blue)](https://github.com/sdetilly/GameOfThronesWiki/actions)

A **real-world example** of Compose Multiplatform best practices with Clean Architecture. Too many tutorials skip the important stuff—this project shows you how it's actually done.

## 🎯 Why This Project?

Finding production-quality examples of Compose Multiplatform with proper architecture is hard. This project fills that gap by demonstrating:

- ✅ **Clean Architecture** — Proper separation with repositories, use cases, and view models
- ✅ **Modern Stack** — Built with the latest and greatest KMP technologies
- ✅ **High Test Coverage** — Using Kover to maintain code quality
- ✅ **Real API Integration** — Not another TODO app, uses the [An API of Ice and Fire](https://anapioficeandfire.com/)
- ✅ **Regular Updates** — This is a living project, continuously improved

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Compose Multiplatform** | Shared UI across Android & iOS |
| **Room** | Local database persistence |
| **Ktor** | Network requests |
| **Coroutines** | Async operations |
| **Koin** | Dependency injection |
| **Coil** | Image loading |
| **Kover** | Code coverage reporting |

## 📱 What It Does

Browse the Game of Thrones universe with a beautiful, performant app:

- 📚 **Books** — Explore all books in the series
- 👑 **Characters** — Discover characters and their linked books
- 🏰 **Houses** — View major and minor houses
- ✨ **Smooth Transitions** — Polished animations between screens
- 🎨 **Material Design** — Clean, modern UI/UX

## 🚧 Upcoming Features

- House detail screens with full information (with linked characters and books)
- Character profile images
- House sigil images (if available from API)
- Navigation drawer with random quotes from the Quotes API

## 📊 Code Quality

This project maintains high standards:

- **Architecture** — Clean separation of concerns (data → domain → presentation)
- **Testing** — Comprehensive test coverage tracked with Kover
- **CI/CD** — Automated checks on every commit
- **Documentation** — Code is clear and well-documented

## 🤝 Contributions Welcome!

Found something that could be better? **Please raise an issue!** Whether it's:

- Performance improvements
- Better architectural patterns
- Logic errors or bugs
- Suggestions for cleaner code

This is a learning resource for the community—your feedback makes it better for everyone.

## 🚀 Building the App

### Android
```shell
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

Or use the run configuration in your IDE's toolbar.

### iOS
Open the `/iosApp` directory in Xcode and run, or use the run configuration in your IDE's toolbar.

## 📂 Project Structure

```
/composeApp
  └── /src
      ├── /commonMain      # Shared code for all platforms
      ├── /androidMain     # Android-specific code
      ├── /androidUnitTest # Unit tests for common code
      └── /iosMain         # iOS-specific code
```

## 📖 Learn More

- [Kotlin Multiplatform Documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [An API of Ice and Fire](https://anapioficeandfire.com/)

---

**Built with ❄️ and 🔥 by the KMP community**
