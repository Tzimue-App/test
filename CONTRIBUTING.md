# Contributing to Spring Boot Template

Thank you for your interest in contributing to this project!

## 🛠 Development Workflow

1.  **Fork the repository** and create your branch from `main`.
2.  **Install prerequisites**: Java 25, Docker, and a Google OAuth2 client.
3.  **Run tests**: Ensure all tests pass before submitting a PR.
    ```bash
    ./gradlew test
    ```
4.  **Follow SOLID principles**: Keep your code clean, modular, and well-documented.
5.  **Use meaningful commit messages**: We recommend following [Conventional Commits](https://www.conventionalcommits.org/).

## 🎨 UI/UX Guidelines

- This project uses **Bootstrap 5** and **Thymeleaf**.
- Custom styles should be added to `static/css/app.css`.
- Ensure all new features are responsive and follow the "Rich Aesthetics" goal.

## 🔐 Security First

- Never commit `.env` files or secrets.
- Always use the provided `.env.example` as a template.

## ✅ Pull Request Process

1.  Update the documentation if you change any public APIs or configurations.
2.  The PR must pass all CI checks (tests, linting).
3.  Once approved, it will be merged into `main`.

Happy coding! 🚀
