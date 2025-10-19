When asked about state management or Compose best practices, you can mention:

> "In Tasky, I follow modern Compose patterns recommended by the community:
>
> 1. ScreenRoot + Screen separation: The Root handles framework dependencies (ViewModel, navigation), while the Screen is a pure, stateless composable. This makes the UI previewable, testable, and reusable.
>
> 2. ObserveAsEvents for one-time events: Instead of collecting events directly in LaunchedEffect, I use a custom ObserveAsEvents composable that ensures events like navigation or toasts are consumed exactly once, even during recomposition or configuration changes.
>
> 3. .onStart{} with stateIn(): I initialize ViewModels lazily when the first UI collector subscribes. Combined with WhileSubscribed(5000), this survives configuration changes without restarting expensive operations, while still stopping unnecessary work when the app goes to the background.
>
> These patterns prevent common bugs like duplicate navigation, multiple data fetches, and unnecessary recompositions."
> Summary Table
> Pattern Problem Solved Benefit
> ScreenRoot + Screen Tight coupling, no previews Testable, previewable, reusable UI
> ObserveAsEvents Duplicate events on recomposition Events consumed exactly once
> .onStart{}.stateIn() Data reloaded on rotation Survives config changes, battery efficient
> WhileSubscribed(5000) Unnecessary restarts 5s grace period for rotation
> These practices make your app robust, performant, and maintainable—exactly what Neo is looking for! 🚀

Why Neo?
Because the tech stack aligns perfect with what I’ve been mastering : Kotlin, Jetpack Compose.
"I thrive in high-pressure, fast-paced environments where excellence is the standard, which aligns with Neo's culture"
Reference specific aspects: highly-rated app, 1M+ customers, innovative approach to financial services
"Neo's recognition as Canada's #1 Fastest Growing Company demonstrates the impact you're making in fintech"

3. Your Unique Differentiators
   What sets you apart:
   • Intensive mentorship: Not just self-taught - you received professional-level code reviews every 1-2 days for 10 weeks
   • Real production experience: Contributing to Himigo's app with 3,500+ active users
   • Ownership mindset: You've built complete apps from architecture to deployment
   • Results-driven: Quantifiable improvements (15% performance boost, 95%+ test coverage, 100% reduction in API calls)
   • Team collaboration: Experience with Agile, Git workflows, cross-functional teams
4. Relocation to Calgary
   Be crystal clear and enthusiastic:
   • "I'm fully committed to relocating to Calgary and excited about it"
   • "I understand Neo values in-person collaboration, and I'm ready to be part of that culture"
   • Timeline: "I can relocate within [X weeks/1-2 months] after receiving an offer"
   • Show you've thought it through: "I'm currently in Vancouver, so I'm already familiar with Canadian living, and I'm excited to explore Calgary"
5. Culture Fit Questions
   Expect questions like these:
   "Tell me about a time you worked under high pressure"
   • Use your Himigo experience: Agile sprints, production bugs, tight deadlines
   • Or your DROP TABLE mentorship: Submitting PRs every 1-2 days while maintaining quality
   "Describe how you handle feedback"
   • Perfect answer: Your mentorship experience receiving daily code reviews
   • "I actively seek feedback - during my DROP TABLE mentorship, I received critical code reviews daily and used them to dramatically improve my code quality"
   "Tell me about a time you went above minimum requirements"
   • Tasky project: You didn't just build features, you achieved 95%+ test coverage and implemented offline-first architecture
   • "The requirements were to build an agenda app, but I implemented automatic background sync with WorkManager, reducing manual errors by 25%"
   "How do you stay current with Android development?"
   • Formal mentorship programs (DROP TABLE)
   • Multiple certifications (list 2-3 relevant ones)
   • Building personal projects with latest tech
   • Contributing to production apps
6. Questions About Your Background
   Your React Native experience at Himigo:
   • "While Himigo uses React Native, I'm implementing native Android modules in Kotlin, which keeps my Android skills sharp"
   • "This experience has taught me cross-platform considerations, which is valuable when Neo coordinates iOS and Web engineers"
   Your timeline (gap explanations):
   • You have concurrent customer service work while building your Android career - shows work ethic
   • Education at BCIT (2023-2025) overlaps with your professional development - shows dedication

---

Questions You Should Ask Kenzie
About the role:

1. "What does success look like for this role in the first 3, 6, and 12 months?"
2. "Can you tell me about the team structure? Who would I be working most closely with?"
3. "What are the most exciting projects or features the Android team is currently working on?"
   About Neo's culture:
4. "You mentioned Neo values in-person collaboration - what does a typical week look like for the Android team?"
5. "How does Neo support continued learning and professional development?"
6. "What do you find most rewarding about working at Neo?"
   About next steps:
7. "What are the next steps in the interview process?"
8. "What technical assessments or interviews should I prepare for?"

---

Red Flags to Avoid
❌ Don't say:
• "I'm just looking for any Android job"
• Any uncertainty about relocation
• Criticism of previous employers/mentors
• "I don't have any questions"
✅ Do say:
• Specific reasons why Neo specifically interests you
• Concrete examples with metrics
• Questions that show you've researched the company
• Enthusiasm about the mission and culture

---

Practical Preparation
Before the Interview:
• Test your internet connection, camera, and microphone
• Prepare a professional background (clean, well-lit)
• Have your resume, cover letter, and Neo job posting open for reference
• Prepare notes (but don't read from them obviously)
• Dress professionally (business casual minimum)
During the Interview:
• Join 2-3 minutes early
• Smile and show enthusiasm
• Use the STAR method (Situation, Task, Action, Result) for behavioral questions
• Take brief pauses to think before answering
• Ask clarifying questions if needed
After the Interview:
• Send a thank-you email within 24 hours
• Reference something specific from your conversation
• Reiterate your enthusiasm and fit

🎯 2. Tailored Talking Points (for Common Interview Questions)
💡 “Tell me about yourself”
“I’m an Android developer with over two years of experience building production-ready apps in Kotlin using Jetpack Compose and modern architecture patterns like MVVM and Clean Architecture.
I recently completed the DROP TABLE Mentorship under Philipp Lackner, where I developed a production-grade agenda app with background sync, dependency injection, and automated testing.
I’m currently contributing to a React Native app with over 3,500 users, implementing native Android modules in Kotlin.
I’m passionate about building high-quality Android experiences that scale, and I’m particularly drawn to Neo’s focus on innovation, testing, and delivering world-class financial products.”
Tell us about a project you’re proud of”
Project: Tasky App – Offline-first Agenda Manager
Focus on:
• Challenge: maintaining reliability offline + sync
• Action: used WorkManager + Room + Flows
• Result: reduced scheduling errors by 25%, >95% code coverage
Example answer:
“In Tasky, I implemented offline-first functionality with WorkManager for automatic background sync, ensuring users never lost data. I followed MVVM and dependency injection with Hilt for clean separation of concerns. Through TDD, I achieved 95%+ code coverage and greatly reduced manual errors. It taught me the importance of architecture and testing discipline in delivering stable apps.”
⚙️ “What’s your experience with testing?”
“Testing is core to my development process. I use JUnit and Espresso for unit and UI tests, following TDD principles. In my Tasky app, I automated CI testing via GitHub Actions. Writing testable code also pushes me to maintain clean architecture and smaller, modular components.”
🧩 “How do you handle code reviews?”
“I approach code reviews as a learning exchange — focusing on readability, testability, and design patterns. I received daily reviews during my mentorship, so I’m comfortable both giving and receiving constructive feedback.”
“Why Neo?”
“Neo’s mission to build a more rewarding financial future for Canadians really resonates with me. I also love that you use Kotlin, Jetpack Compose, and GraphQL — the same stack I’ve been using. Plus, the ownership and growth mindset culture aligns perfectly with how I like to work: building, learning, and iterating fast.”
“How do you leverage AI tools like GitHub Copilot?”
“I use Copilot to accelerate repetitive coding tasks, generate test scaffolding, and improve code reviews. I see AI as a productivity booster — not a crutch — and always verify and refine what it produces.”
🧱 1. 1-Minute Elevator Pitch (Introduction)
Use this when they say: “Tell me about yourself.”
“I’m an Android developer with over two years of experience building production-ready apps using Kotlin, Jetpack Compose, and modern architectures like MVVM and Clean Architecture.
I recently completed an advanced mentorship under Philipp Lackner, where I built a fully offline-first agenda app using WorkManager, Hilt, and Coroutines — achieving over 95% code coverage through automated testing.
Currently, I’m working at Himigo on a React Native app with over 3,500 users, implementing native Android modules and improving app performance.
What excites me most about Neo is your focus on innovation, testing discipline, and using Kotlin + Compose — the same tools I’m passionate about. I’m drawn to your mission of making finance more rewarding and to the opportunity to grow alongside top engineers in a fast-paced environment.”
🎯 Tips:
• Keep tone enthusiastic but composed.
• Emphasize Neo’s stack + your testing/architecture skills.
• Finish with alignment to Neo’s mission.

---

💬 2. Mock Interview Questions + Model Answers
🧠 Technical Questions

1. What’s your approach to app architecture?
   “I prefer MVVM with Clean Architecture because it separates concerns across layers — UI, domain, and data — making the code more testable and maintainable. I use ViewModels to handle state, repositories for data sources, and dependency injection with Hilt for modularity. This setup makes refactoring or scaling features easier without breaking the app.”

---

2. How do you manage state in Jetpack Compose?
   “I use remember and mutableStateOf for local UI state, and StateFlow or LiveData in ViewModels for screen-level state. For derived or computed states, I rely on derivedStateOf to optimize recompositions. The key is to keep state single-sourced and predictable.”

---

3. Explain how you use Coroutines and Flows.
   “Coroutines let me handle background tasks efficiently — for example, network calls or database queries. I use structured concurrency in ViewModels with viewModelScope.launch.
   For reactive data streams, I use Flows because they integrate well with Compose. They allow me to observe changes and automatically update the UI while keeping it lifecycle-aware.”

---

4. What’s your approach to testing Android apps?
   “I follow TDD when possible and prioritize testing across all layers:
   • Unit tests for ViewModels and repositories (using JUnit and Mockito)
   • Integration tests for database and API interactions
   • UI tests with Espresso
   I’ve also used GitHub Actions for continuous integration to run automated tests on every pull request.”

---

5. How do you handle dependency injection?
   “I use Hilt for dependency injection. It simplifies setup by handling component lifecycles automatically. I structure modules by layers — for example, NetworkModule, DatabaseModule, and RepositoryModule — to keep dependencies organized and easily testable.”

---

6. How do you ensure code quality in a team setting?
   “Through code reviews, clear commit messages, and enforcing linting + test coverage thresholds in CI. I also leverage tools like GitHub Copilot to speed up repetitive tasks, but always review its suggestions critically.”

---

⚙️ Scenario Questions

1. You’re asked to release a feature quickly. How do you balance speed and quality?
   “I focus on delivering a minimal, testable version that meets core requirements. I write unit tests for critical logic to prevent regressions, and if needed, I log technical debt for post-release refactoring. Speed is important, but I don’t compromise on maintainability.”

---

2. A teammate suggests using a new library you’re unfamiliar with. What do you do?
   “I evaluate it by checking its documentation, community support, and compatibility with our architecture. I might create a small prototype or test branch to assess its benefits. I’m always open to learning but cautious about introducing dependencies that could affect stability.”

---

3. Describe a time you improved app performance.
   “In my Holidays App, I reduced API calls by 100% for static data by implementing local caching with Room. This improved load times and reduced network usage significantly.”

---

🧍 Behavioral Questions

1. Tell me about a time you received constructive feedback.
   “During my mentorship, code reviews were daily and detailed. At first, I struggled with structuring ViewModels cleanly. Through feedback and iteration, I improved the separation of concerns in my code, which made my architecture more scalable. I now actively seek reviews early.”

---

2. Tell me about a time you took initiative.
   “In my mentorship, I proposed integrating WorkManager for automatic background sync instead of manual refresh. It was new to me, but it reduced scheduling errors by 25%. That experience reinforced the value of research and ownership.”

---

3. How do you handle tight deadlines?
   “I break the work into smaller deliverables, prioritize based on impact, and communicate progress clearly. If something risks the timeline, I discuss trade-offs early with the team.”

The viewModel shouldn’t be pass directly to the screen because the preview will brake instead we should pass the uiState that way we can easily modify the uiState arguments. Also because this allows us to test the screen in isolation without extra dependencies to external classes.
