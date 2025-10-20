# 🎭 Mock Interview - Neo Financial with Kenzie Sharko

## Interview Details

**Interviewer:** Kenzie Sharko - Senior Talent Acquisition Partner at Neo Financial  
**Role:** Intermediate Android Developer  
**Duration:** 30 minutes  
**Location:** Calgary, Alberta (On-site)  
**Interview Type:** Recruiter Screening Call

---

## Interview Structure (30 min total)

```
Opening (2 min)
    ↓
About You (3-4 min)
    ↓
Why Neo? (2-3 min)
    ↓
Tasky Project (4-5 min)
    ↓
Testing Experience (3-4 min)
    ↓
Code Reviews (2-3 min)
    ↓
GraphQL/Learning (2 min)
    ↓
Relocation (1-2 min)
    ↓
AI Tools (2 min)
    ↓
Fast-Paced Environment (2 min)
    ↓
Team Collaboration (2 min)
    ↓
Strengths/Growth (2 min)
    ↓
Your Questions (5-6 min)
    ↓
Closing (1 min)
```

---

## Opening (2 minutes)

### **KENZIE:**

"Hi Andres! Thanks so much for taking the time to speak with me today. I'm Kenzie, a Senior Talent Acquisition Partner here at Neo Financial. How are you doing today?"

### **YOUR ANSWER:**

> "I'm doing great, thanks Kenzie! I'm really excited to speak with you about the Android Developer position. Neo's mission to build a more rewarding financial future for Canadians really resonates with me, and I'm thrilled to learn more about the role."

---

### **KENZIE:**

"Wonderful! So this will be about 30 minutes. I'll ask you some questions about your background, we'll dive into your experience with Android development, talk about the role, and leave time for any questions you have for me. Sound good?"

### **YOUR ANSWER:**

> "That sounds perfect. I'm ready!"

---

## Question 1: Tell Me About Yourself (3-4 min)

### **KENZIE:**

"Great! Let's start with you telling me a bit about yourself and your journey into Android development."

### **YOUR ANSWER:**

> "Absolutely! I'm an Android developer with over 2 years of experience building production-ready applications using Kotlin and Jetpack Compose.
>
> My journey into Android started with my education—I completed a Diploma in Wireless and Mobile App Development at CICCC, followed by a Computer Systems certification at BCIT. But what really accelerated my skills was completing the DROP TABLE Mentorship Program with Philipp Lackner, a Senior Android Developer. For 10 weeks, I received code reviews every 1-2 days on a production-ready app called Tasky.
>
> Currently, I'm working at Himigo as a Mobile Developer contributing to a React Native app with 3,500+ active users, where I implement native Android modules using Kotlin. But my passion is really in native Android development with Compose.
>
> What I love about Android is solving complex problems—like building offline-first architectures, implementing background sync, and creating smooth user experiences. That's exactly what drew me to this role at Neo."

**KEY POINTS TO INCLUDE:**

- ✅ 2+ years experience
- ✅ Mention mentorship with Philipp Lackner
- ✅ Current role at Himigo
- ✅ Passion for native Android
- ✅ Connect to Neo's role

---

## Question 2: Why Neo Financial? (2-3 min)

### **KENZIE:**

"That's great background! So tell me, what specifically interests you about Neo Financial and this role?"

### **YOUR ANSWER:**

> "Three main things excite me about Neo:
>
> **First, the mission.** Building a more rewarding financial future for all Canadians is something I can get behind. Financial services should be accessible and rewarding, and Neo is proving that's possible with 1M+ customers and a highly-rated app.
>
> **Second, the technical challenges.** You're one of the fastest-growing fintechs in Canada, which means complex scalability challenges, high standards for quality, and cutting-edge technology. The stack—Kotlin, Compose, GraphQL—is exactly what I want to work with. I have extensive experience with Kotlin and Compose, and I'm excited to learn GraphQL since my Retrofit experience will transfer well.
>
> **Third, the culture.** The job posting mentions high standards, code reviews, and AI-powered tools like GitHub Copilot—which I already use daily. I thrive in environments that push me to grow, and Neo's emphasis on 'blazing trails through uncertainty' rather than following well-paved paths really resonates with how I approach development."

**KEY POINTS TO INCLUDE:**

- ✅ Mission alignment
- ✅ Technical excitement (mention specific stats like 1M+ customers)
- ✅ Culture fit (code reviews, high standards)
- ✅ Growth mindset

---

## Question 3: Walk Me Through Tasky (4-5 min)

### **KENZIE:**

"I see Tasky prominently on your resume. This was part of your mentorship with Philipp Lackner, right? Can you walk me through what makes this project special and what you learned?"

### **YOUR ANSWER:**

> "Absolutely! Tasky is an offline-first agenda management app I built during a 10-week intensive mentorship. What makes it special is the level of rigor—I received code reviews every 1-2 days from a Senior Android Developer, which taught me to write production-quality code.
>
> **Technical Implementation:**
>
> - **Offline-first architecture:** Users can create, update, delete items without connectivity. Everything saves locally first, then syncs in the background using WorkManager with retry logic.
> - **Clean Architecture:** Feature-based modules (auth, agenda) with clear Data/Domain/Presentation layer separation.
> - **Modern state management:** MVVM with MVI elements—StateFlow for state, Channel for one-time events, sealed interfaces for type-safe actions.
> - **Centralized navigation:** All navigation logic in one place for testability.
> - **Comprehensive testing:** 80+ unit tests with fake implementations covering ViewModels, repositories, and business logic.
>
> **What I Learned:**
>
> - How to structure code for scalability and team collaboration
> - Offline-first patterns with conflict resolution
> - Background work with WorkManager (dedicated workers, exponential backoff)
> - Exact alarm scheduling with AlarmManager
> - Writing testable code from day one
>
> The daily code reviews taught me to think about edge cases, maintainability, and how other developers will interact with my code—essential for team environments like Neo."

**KEY POINTS TO INCLUDE:**

- ✅ Mentorship context (10 weeks, code reviews every 1-2 days)
- ✅ Offline-first architecture
- ✅ Clean Architecture
- ✅ 80+ unit tests
- ✅ What you learned (team collaboration, testing, patterns)
- ✅ Connect to Neo's team environment

---

## Question 4: Testing Experience (3-4 min)

### **KENZIE:**

"I notice the job posting emphasizes 'actively work on high-quality unit and UI testing.' Can you tell me about your testing experience?"

### **YOUR ANSWER:**

> "Testing is fundamental to how I develop. In Tasky alone, I have **80+ unit tests** covering ViewModels, repositories, and business logic.
>
> **My Testing Approach:**
>
> - **Fake implementations instead of mocks** - More maintainable and behave like real components. My FakeAgendaRepository actually emits Flow updates just like Room would.
> - **Edge case coverage** - I don't just test happy paths. For example, my InputValidator tests cover boundary conditions (8 vs 9 character passwords), unicode characters, emojis, empty inputs, and special characters.
> - **Proper coroutine testing** - Using `runTest`, `advanceUntilIdle()`, and proper Flow collection patterns.
> - **Testable architecture** - I design for testability from the start using dependency injection and interface abstractions.
>
> **In my resume, I mention 95%+ code coverage in Tasky** because I follow TDD principles—write tests as I build features, not as an afterthought.
>
> I also have experience with integration tests and UI tests using Espresso. Testing gives me confidence to refactor and proves my code is maintainable—exactly what team environments need."

**KEY POINTS TO INCLUDE:**

- ✅ 80+ unit tests
- ✅ Fakes over mocks (explain why)
- ✅ Edge case coverage examples
- ✅ TDD principles
- ✅ 95%+ coverage
- ✅ Connect to team benefits

---

## Question 5: Code Reviews (2-3 min)

### **KENZIE:**

"The role involves 'meaningful code reviews with in-depth yet constructive criticism.' How do you approach giving and receiving feedback?"

### **YOUR ANSWER:**

> "I love code reviews! During my mentorship, I received reviews every 1-2 days for 10 weeks, which taught me to both give and receive feedback effectively.
>
> **When Receiving Feedback:**
>
> - I see it as a learning opportunity, not criticism
> - I ask clarifying questions to understand the 'why' behind suggestions
> - I'm comfortable defending my decisions if I have solid reasoning, but also flexible when shown better approaches
> - I implement feedback quickly and thoroughly
>
> **When Giving Feedback:**
>
> - I focus on architecture, testability, and maintainability—not just syntax
> - I explain the 'why' behind suggestions
> - I look for potential edge cases or error scenarios
> - I highlight what was done well, not just what needs improvement
> - I'm constructive and collaborative
>
> **In my architecture, I make code review-friendly by:**
>
> - Clear naming and structure
> - Type-safe sealed interfaces
> - Immutable state
> - Well-documented complex logic
> - Tests that serve as documentation
>
> I believe great code reviews make the whole team better, and I'm excited about Neo's emphasis on this."

**KEY POINTS TO INCLUDE:**

- ✅ Mentorship experience (10 weeks of reviews)
- ✅ How you receive feedback (learning opportunity)
- ✅ How you give feedback (constructive, explain why)
- ✅ Make code review-friendly
- ✅ Team benefits

---

## Question 6: GraphQL Experience (2 min)

### **KENZIE:**

"Our stack uses GraphQL. I see you have experience with Retrofit and REST APIs. How do you feel about learning GraphQL?"

### **YOUR ANSWER:**

> "I haven't worked with GraphQL in production yet, but I'm genuinely excited to learn it.
>
> **Why I'm confident:**
>
> - My Retrofit experience gives me a strong foundation in API integration
> - I understand reactive patterns with Kotlin Flow, which transfers well to GraphQL subscriptions
> - I'm familiar with data layer patterns—repositories, data sources, mappers—which are similar
> - I've used GitHub Copilot extensively, which can help accelerate the learning curve
>
> **My approach to learning new technologies:**
>
> - I research best practices (likely Apollo for Android?)
> - I build small examples to understand patterns
> - I ask questions and learn from code reviews
> - I document patterns for the team
>
> In my mentorship, I learned WorkManager, advanced Flow operators, and testing patterns I'd never used before. I'm a fast learner who thrives on challenges, and GraphQL is exactly the kind of technology I want to master."

**KEY POINTS TO INCLUDE:**

- ✅ Honest about not having GraphQL experience
- ✅ Confident you can learn (evidence from past)
- ✅ Retrofit transfers well
- ✅ Mention Apollo (shows you've researched)
- ✅ Growth mindset

---

## Question 7: Relocation to Calgary (1-2 min)

### **KENZIE:**

"The role requires relocation to Calgary and is fully on-site. How do you feel about that?"

### **YOUR ANSWER:**

> "I'm absolutely willing to relocate to Calgary. In fact, I'm excited about it!
>
> **Why I'm ready:**
>
> - I understand Neo's culture emphasizes in-person collaboration, which I value for knowledge sharing and team cohesion
> - I'm currently in Vancouver, so I'm familiar with Western Canada
> - I have no ties preventing relocation
> - I'm flexible and can relocate quickly (within 3-4 weeks of an offer)
>
> I see the on-site requirement as an opportunity, not a challenge. Some of my best learning has come from in-person collaboration—quick questions, whiteboarding sessions, pair programming. For a fast-paced startup like Neo, being co-located makes sense.
>
> If I receive an offer, how much notice would Neo typically provide for relocation?"

**KEY POINTS TO INCLUDE:**

- ✅ Enthusiastic about Calgary
- ✅ See on-site as opportunity
- ✅ No blockers to relocation
- ✅ Can move quickly
- ✅ Ask practical question about timeline

---

## Question 8: AI-Powered Tools (2 min)

### **KENZIE:**

"The posting mentions leveraging GitHub Copilot. What's your experience with AI-powered development tools?"

### **YOUR ANSWER:**

> "I use GitHub Copilot daily and it's actually mentioned in my resume under 'Innovative Mindset.'
>
> **How I Use Copilot:**
>
> - **Boilerplate reduction:** Generating test setups, repetitive code patterns
> - **Test generation:** Creating edge case tests I might not think of
> - **Code completion:** Faster implementation of standard patterns
> - **Learning:** Asking Copilot about unfamiliar APIs or patterns
> - **Code reviews:** Using it to suggest improvements or catch issues
>
> **But I'm careful:**
>
> - I don't blindly accept suggestions
> - I review generated code for correctness and style
> - I ensure tests actually test what I intend
> - I maintain code quality standards
>
> In my Calories Tracker project, I specifically adopted Copilot for code generation and testing automation, which improved my velocity without sacrificing quality.
>
> I see AI tools as force multipliers, not replacements for understanding. They help me focus on architecture and problem-solving rather than syntax."

**KEY POINTS TO INCLUDE:**

- ✅ Daily use of Copilot
- ✅ Specific use cases
- ✅ Balanced approach (not blindly accepting)
- ✅ Improved velocity
- ✅ Force multiplier, not replacement

---

## Question 9: Fast-Paced Environment (2 min)

### **KENZIE:**

"Neo is described as complex, fast-paced, and high-pressure. How do you thrive in environments like that?"

### **YOUR ANSWER:**

> "I actually thrive in fast-paced environments! Here's why:
>
> **Evidence from my experience:**
>
> - **Himigo:** Contributing to a production app with 3,500+ active users requires quick iteration and reliability
> - **Mentorship:** 10 weeks of intensive development with code reviews every 1-2 days taught me to deliver quality quickly
> - **Previous role at Sequoia:** 4 years in a high-volume, fast-paced restaurant environment taught me to solve problems under pressure
>
> **How I manage complexity:**
>
> - **Break down problems:** I tackle big challenges in smaller, manageable pieces (evident in my Clean Architecture approach)
> - **Prioritize ruthlessly:** Focus on what delivers customer value
> - **Communicate clearly:** Keep stakeholders informed of progress and blockers
> - **Stay calm under pressure:** I've debugged production issues and handled tight deadlines
>
> **What energizes me:**
>
> - Solving hard problems
> - Seeing my code impact real users
> - Learning from talented teammates
> - Shipping features that matter
>
> The mentorship program was high-pressure—building production-quality features with frequent reviews—and I loved it. Neo's environment sounds like exactly where I want to be."

**KEY POINTS TO INCLUDE:**

- ✅ Evidence you thrive under pressure
- ✅ How you manage complexity
- ✅ What energizes you
- ✅ Sequoia experience (problem-solving under pressure)
- ✅ Enthusiasm for Neo's environment

---

## Question 10: Team Collaboration (2 min)

### **KENZIE:**

"You'll be working closely with iOS and Web engineers. Tell me about your cross-functional collaboration experience."

### **YOUR ANSWER:**

> "I have strong cross-functional collaboration experience:
>
> **At Himigo:**
>
> - Working with a 3-person development team using Git workflows
> - Daily standups and sprint planning
> - Pull requests and code reviews
> - Collaborating on app stability and feature development
>
> **What I bring to cross-functional teams:**
>
> - **Communication:** I can translate technical concepts for non-technical stakeholders
> - **Empathy:** I understand iOS and Web have different constraints
> - **Consistency:** I prioritize consistent UX across platforms (mentioned in job posting)
> - **Documentation:** Clear docs help everyone stay aligned
> - **Flexibility:** I'm open to feedback and different approaches
>
> **Example from BPM ANDINA:**
>
> - Worked with cross-functional teams to resolve technical challenges
> - Translated complex issues into clear solutions for clients
> - Ensured compliance while meeting business needs
>
> I believe the best products come from collaborative teams where everyone challenges each other constructively. The job posting mentions 'honest conversations and constructive input'—that's exactly how I work."

**KEY POINTS TO INCLUDE:**

- ✅ Himigo team experience
- ✅ Cross-functional skills
- ✅ Reference job posting (consistent UX)
- ✅ BPM ANDINA example
- ✅ Collaborative mindset

---

## Question 11: Strengths & Growth Areas (2 min)

### **KENZIE:**

"What would you say is your biggest strength as an Android developer, and one area you're actively working to improve?"

### **YOUR ANSWER:**

**Strength:**

> "My biggest strength is **architectural thinking coupled with testability.** I don't just write code that works—I design systems that scale. In Tasky, I implemented Clean Architecture with feature modules, centralized navigation, and 80+ unit tests. This shows I think about maintainability, team collaboration, and long-term code health, not just immediate features.
>
> This strength comes from my mentorship where every decision was scrutinized in code reviews. I learned to justify architectural choices and design for teams, not just solo work."

**Area for Growth:**

> "One area I'm actively improving is **GraphQL.** I have strong REST API experience with Retrofit, but I haven't used GraphQL in production. However, I'm a fast learner—in my mentorship, I mastered WorkManager, advanced Flow operators, and testing patterns I'd never used before.
>
> I've already started researching Apollo Android and GraphQL patterns. My understanding of reactive programming with Flows will transfer well to GraphQL subscriptions. I see this as an exciting learning opportunity, and I'm confident I'll be productive quickly with the team's support."

**KEY POINTS TO INCLUDE:**

- ✅ Strength: Architectural thinking + testability
- ✅ Evidence from Tasky
- ✅ Growth area: GraphQL (honest)
- ✅ Confidence you can learn
- ✅ Already researching (Apollo)

---

## Question 12: Challenging Technical Problem (2 min)

### **KENZIE:**

"Can you give me an example of a time you faced a challenging deadline or technical problem? How did you handle it?"

### **YOUR ANSWER:**

> "Great question! During my mentorship, I faced a challenging deadline implementing the offline-first synchronization pattern.
>
> **The Challenge:**
>
> - Build background sync with WorkManager
> - Handle conflict resolution between local and remote data
> - Schedule exact alarms for notifications
> - All while maintaining code quality for review
>
> **My Approach:**
>
> 1. **Break it down:** I split the problem into smaller pieces—pending sync queue, dedicated workers per operation, retry logic
> 2. **Research best practices:** I studied WorkManager constraints, backoff strategies, AlarmManager APIs
> 3. **Iterate quickly:** Built MVPs, got feedback, refined
> 4. **Test thoroughly:** Wrote tests for offline scenarios, error cases, edge conditions
> 5. **Communicate:** Documented my approach and decisions
>
> **Result:**
>
> - Delivered a robust offline-first system that handles network failures gracefully
> - Received positive code review feedback
> - Learned patterns I now apply to every project
>
> This experience taught me that pressure brings out my best work when I stay organized, communicate clearly, and focus on delivering value."

**KEY POINTS TO INCLUDE:**

- ✅ Specific technical challenge
- ✅ Your approach (methodical, researched)
  > - ✅ Positive outcome
- ✅ What you learned
- ✅ Shows problem-solving under pressure

---

## Question 13: Deep Technical - Architecture (3-4 min)

### **KENZIE:**

"The role requires someone fluent in Kotlin and app architecture principles. Can you briefly explain your architecture approach in Tasky?"

### **YOUR ANSWER:**

> "Absolutely! Tasky follows **Clean Architecture** organized by feature.
>
> **Structure:**
>
> - **Feature modules** (auth/, agenda/, core/) - Each feature is self-contained
> - **Three layers per feature:**
>   - **Presentation:** Composables + ViewModels + StateFlow/Channel for events
>   - **Domain:** Repository interfaces + business models (platform-independent)
>   - **Data:** Repository implementations + local (Room) + remote (Retrofit) data sources
>
> **State Management (MVVM + MVI):**
>
> - ViewModels hold single immutable `ViewState` data class
> - User actions modeled as sealed interfaces (type-safe)
> - One-time events (navigation, errors) via Channel → Flow
> - `WhileSubscribed(5000)` to survive configuration changes
>
> **Modern Patterns:**
>
> - **ScreenRoot + Screen separation** for testability and preview support
> - **Centralized navigation** where screens emit NavigationEvents, MainActivity handles actual navigation
> - **Offline-first repository** pattern with pending sync queue
>
> **Why this architecture:**
>
> - **Testable:** 80+ unit tests prove it
> - **Scalable:** Easy for teams to work in parallel
> - **Maintainable:** Clear boundaries, explicit dependencies
> - **Production-ready:** Handles real-world challenges (offline, sync, process death)
>
> This is the same type of architecture I'd bring to Neo's codebase."

**KEY POINTS TO INCLUDE:**

- ✅ Clean Architecture explanation
- ✅ Feature-based modules
- ✅ State management approach
- ✅ Modern patterns
- ✅ Why (testable, scalable)
- ✅ Connect to Neo

---

## Question 14: Why Testing Matters (2-3 min)

### **KENZIE:**

"You mention 95%+ code coverage and 80+ unit tests. Why is testing so important to you?"

### **YOUR ANSWER:**

> "Testing is important to me for three reasons:
>
> **1. Confidence:** Tests let me refactor fearlessly. When I improve architecture or add features, tests catch regressions immediately.
>
> **2. Documentation:** My tests show how components should behave. New teammates can read tests to understand intent.
>
> **3. Quality Assurance:** Tests prove my code works in success scenarios, error cases, and edge conditions.
>
> **My Approach in Tasky:**
>
> - **80+ tests** across ViewModels (45 tests), business logic (25 tests), data sources (10+ tests)
> - **Fake implementations** instead of mocks—more maintainable and realistic
> - **Edge case coverage** - empty inputs, unicode, concurrent operations, offline scenarios
> - **Proper async testing** - RunTest, advanceUntilIdle for coroutines, Flow collection patterns
>
> **Example:** My InputValidator has 25+ tests covering everything from valid/invalid emails to password boundary conditions with unicode support. These edge cases catch bugs users actually encounter.
>
> For Neo, with 1M+ customers and high standards, comprehensive testing ensures we deliver reliable features that delight users, not frustrate them. It's not just about minimum requirements—it's about exceeding customer expectations."

**KEY POINTS TO INCLUDE:**

- ✅ Three reasons (confidence, documentation, quality)
- ✅ 80+ tests breakdown
- ✅ Fakes vs mocks
- ✅ Specific example (InputValidator)
- ✅ Connect to Neo's customers

---

## Question 15: Availability (1 min)

### **KENZIE:**

"When would you be available to start if we were to move forward?"

### **YOUR ANSWER:**

> "I'd need to give 2 weeks notice at my current position, so I could start in approximately 3 weeks. However, I'm flexible and can work with Neo's timeline.
>
> For relocation, I can coordinate the move to Calgary within that timeframe or shortly after, depending on what works best for Neo. I'm committed to making this transition smooth and professional."

---

## Question 16: Salary Expectations (1 min)

### **KENZIE:**

"Do you have any salary expectations for this role?"

### **YOUR ANSWER:**

> "I'm looking for a competitive salary aligned with the intermediate Android developer level in Calgary's market. Based on my research and my 2+ years of experience with modern Android development, production apps, and comprehensive testing skills, I'm thinking in the range of **$75,000 to $95,000 CAD** annually.
>
> However, I'm most excited about the opportunity, growth potential, and equity stake at Neo. I'm open to discussion once we align on the role fit. What's the budgeted range for this position?"

**NOTE:** Research current Calgary market rates before interview. This range is approximate.

---

## 🎯 YOUR QUESTIONS FOR KENZIE (5-6 min)

### **KENZIE:**

"Great! Those are all my questions. What questions do you have for me?"

---

### **Question 1: Team Structure** ⭐ (Priority!)

**YOU ASK:**

> "Can you tell me about the Android team structure? How many Android developers are there, and how do they collaborate with iOS and backend teams?"

**Why ask:** Shows interest in team dynamics, collaboration

**What you're looking for:**

- Team size
- Collaboration model
- Communication patterns
- Code sharing strategies

---

### **Question 2: Day-to-Day** ⭐ (Priority!)

**YOU ASK:**

> "What would a typical day look like for someone in this role? Are there daily standups, sprint planning, or other regular rituals?"

**Why ask:** Shows you're thinking practically about the role

**What you're looking for:**

- Daily routine
- Meeting cadence
- Sprint structure
- Work-life balance

---

### **Question 3: Success Metrics** ⭐

**YOU ASK:**

> "How would you measure success for someone in this role in the first 3, 6, and 12 months?"

**Why ask:** Shows you're goal-oriented and want to deliver

**What you're looking for:**

- Onboarding expectations
- Performance metrics
- Growth trajectory
- What "success" looks like

---

### **Question 4: Learning & Development**

**YOU ASK:**

> "You mentioned learning GraphQL would be part of the role. What learning and development opportunities does Neo provide? Is there support for learning GraphQL, conferences, courses, or dedicated learning time?"

**Why ask:** Shows growth mindset, long-term thinking

**What you're looking for:**

- Learning budget
- Conference attendance
- Internal training
- Time for learning

---

### **Question 5: Next Steps** ⭐ (Always ask!)

**YOU ASK:**

> "What are the next steps in the interview process, and what's the expected timeline?"

**Why ask:** Shows you're interested and helps you plan

**What you're looking for:**

- Technical interview next?
- How many rounds?
- Timeline to decision
- When to expect feedback

---

## Closing (1 min)

### **KENZIE:**

"Those are great questions! Is there anything else you'd like me to know before we wrap up?"

### **YOUR FINAL STATEMENT:**

> "Just that I'm genuinely excited about this opportunity. Neo's mission, the technical challenges, and the culture all align with what I'm looking for. I have the exact stack you need—Kotlin, Compose, Hilt, Coroutines—and I've proven I can build production-quality, well-tested code through my mentorship and current work.
>
> I'm ready to contribute immediately, eager to learn GraphQL, and committed to relocating to Calgary. Thank you for your time today, Kenzie. I'm looking forward to the next steps!"

---

## 📧 Thank You Email (Send Within 24 Hours)

```
Subject: Thank you - Android Developer Interview

Hi Kenzie,

Thank you for taking the time to speak with me today about the Intermediate Android Developer role at Neo Financial. I really enjoyed learning more about the team, the technical challenges, and Neo's mission to build a better financial future for Canadians.

Our conversation reinforced my excitement about the opportunity. The emphasis on code quality, testing, and leveraging modern tools like GitHub Copilot aligns perfectly with how I approach development. I'm particularly excited about learning GraphQL and contributing to an app that serves 1M+ Canadians.

I'm confident my experience with Kotlin, Jetpack Compose, offline-first architecture, and comprehensive testing (80+ unit tests in Tasky) would allow me to contribute immediately to the Android team. I'm ready to relocate to Calgary and eager to be part of Neo's incredible growth.

[If she mentioned something specific, reference it here]:
"I was especially interested when you mentioned [specific detail about team/role/challenge]. This aligns perfectly with my experience in [relevant experience]."

Please don't hesitate to reach out if you need any additional information. I look forward to the next steps in the interview process!

Best regards,
Andres Arevalo
(236) 986-3592
andresfelipear@gmail.com
```

---

## 📊 Interview Performance Checklist

**After the interview, check if you:**

- ✅ Kept answers concise (2-4 min each, not rambling)
- ✅ Mentioned "80+ unit tests" multiple times
- ✅ Referenced specific Tasky features
- ✅ Showed enthusiasm for Neo's mission
- ✅ Demonstrated growth mindset (GraphQL)
- ✅ Asked 3-5 thoughtful questions
- ✅ Connected technical skills to business impact (1M+ customers)
- ✅ Showed you researched Neo (fastest growing, #1 in Canada)
- ✅ Expressed commitment to relocation
- ✅ Ended with strong closing statement

---

## 🎯 Red Flags to Avoid

**Don't:**

- ❌ Badmouth current employer (Himigo)
- ❌ Say "I don't know" without follow-up
- ❌ Ramble (watch time—answers should be 2-4 min max)
- ❌ Focus only on salary
- ❌ Seem uncertain about relocation
- ❌ Make up experience you don't have
- ❌ Appear unenthusiastic
- ❌ Forget to ask questions

**Do:**

- ✅ Be enthusiastic and genuine
- ✅ Give specific examples from Tasky
- ✅ Connect your skills to Neo's needs
- ✅ Show growth mindset
- ✅ Ask thoughtful questions
- ✅ End strong

---

## 💡 Quick Reference During Interview

### **Key Stats to Remember:**

- **Tasky:** 80+ unit tests, offline-first, Clean Architecture, 95%+ coverage
- **Mentorship:** 10 weeks, code reviews every 1-2 days, Philipp Lackner
- **Current:** Himigo, 3,500+ users, React Native + Kotlin native modules
- **Neo:** 1M+ customers, #1 fastest growing, 700+ team, 10K+ partners
- **Stack Match:** Kotlin ✅, Compose ✅, Hilt ✅, Coroutines ✅, GraphQL 🔄

### **Your Unique Selling Points:**

1. Perfect stack match (Kotlin, Compose, Hilt)
2. Production experience (Himigo 3,500+ users)
3. Mentorship credential (Philipp Lackner)
4. Comprehensive testing (80+ tests)
5. Willing to relocate (no hesitation)
6. AI tools experience (GitHub Copilot)
7. Growth mindset (ready to learn GraphQL)

---

## 🎬 Practice Tips

### **Before Interview:**

1. **Read this document 3 times** out loud
2. **Time yourself** - answers should be 2-4 min each
3. **Practice 30-second version** of each answer (in case Kenzie says "briefly")
4. **Prepare questions** - have 5 ready, ask 3-4
5. **Research Neo** - read latest news, app reviews

### **Day Of:**

1. **30 min before:** Review this document one last time
2. **15 min before:** Practice 30-second pitch
3. **5 min before:** Breathe, smile, be confident
4. **During:** Smile (even on phone—it shows in your voice), take notes, be enthusiastic

---

## 🚨 Emergency Answers

**If you blank on a question:**

**"Tell me about Tasky"**
→ "Offline-first agenda app, Clean Architecture, 80+ tests, 10-week mentorship with daily code reviews"

**"Why Neo?"**
→ "Mission (1M+ Canadians), technical challenges (fastest growing fintech), culture (high standards, code reviews)"

**"Testing?"**
→ "80+ unit tests, fakes not mocks, edge case coverage, TDD principles, 95%+ coverage"

**"GraphQL?"**
→ "Haven't used in production, but confident I can learn. Retrofit experience transfers, Flow knowledge helps, fast learner proven in mentorship"

**"Fast-paced?"**
→ "I thrive under pressure. Evidence: Himigo (3,500 users), mentorship (reviews every 1-2 days), Sequoia (high-volume environment)"

---

## ⏱️ Time Management

**Watch the clock!**

- ✅ Answers should be **2-4 minutes** max
- ✅ If Kenzie says "briefly" → **30-60 seconds**
- ✅ Leave **5-6 minutes** for your questions
- ✅ Don't monopolize time

**If you're running long:**

- "I could go into more detail, but I want to be mindful of time. Happy to elaborate if helpful!"

---

## 🎯 Success Indicators

**You did well if:**

- ✅ Kenzie engaged and asked follow-ups
- ✅ She mentioned next steps specifically
- ✅ She seemed excited about your experience
- ✅ The conversation felt natural, not interrogative
- ✅ You got your questions answered
- ✅ She mentioned timeline or process
- ✅ You felt confident and prepared

---

## 📝 Notes Template (Use During Interview)

**Take notes on:**

- [ ] Team size and structure
- [ ] Next interview steps
- [ ] Timeline for decision
- [ ] Any concerns Kenzie mentioned
- [ ] Specific details to reference in thank you email
- [ ] Names of people you might interview with next
- [ ] Questions you want to ask in technical round

---

## 🚀 Final Prep

### **30 Min Before Interview:**

1. Read this entire document once
2. Practice your 30-second pitch
3. Review your questions for Kenzie
4. Get water, use bathroom
5. Find quiet space with good internet
6. Test audio/video if virtual
7. Have pen and paper ready for notes

### **5 Min Before:**

1. Breathe deeply (3 deep breaths)
2. Smile (gets you in positive mood)
3. Review key stats (80+ tests, 1M+ customers, 10 weeks mentorship)
4. Remember: You're qualified and prepared
5. Join call 2 minutes early

---

## 🎭 Mindset

**Remember:**

- This is a **conversation**, not an interrogation
- Kenzie wants you to succeed (good hires make her look good!)
- You have impressive credentials
- Your enthusiasm matters as much as skills
- It's okay to pause and think before answering
- Smile and be yourself

**You've got this!** 🚀

---

## 📌 Quick Reference Card

**Copy this and have it visible during interview:**

```
TASKY STATS:
- 80+ unit tests (95%+ coverage)
- Offline-first architecture
- Clean Architecture (feature modules)
- 10-week mentorship, reviews every 1-2 days
- Philipp Lackner (Senior Android Dev)

NEO STATS:
- 1M+ customers
- #1 fastest growing company 2024
- 700+ team members
- 10K+ retail partners

YOUR PITCH:
"Android dev, 2+ years, Kotlin + Compose expert.
Built Tasky (offline-first, 80+ tests) in mentorship.
Currently at Himigo (3,500 users).
Perfect stack match. Ready to relocate.
Excited about Neo's mission!"

YOUR QUESTIONS:
1. Team structure?
2. Typical day?
3. Success metrics?
4. Learning opportunities?
5. Next steps?
```

---

**Good luck, Andres! You're going to crush this interview!** 🎯🚀🎉
