# LawEZY

LawEZY is a comprehensive, modern legal platform designed to bridge the gap between clients seeking legal help and qualified professionals. It provides digital resources, community interaction, seamless communication, and automated assistance.

> [!NOTE] 
> **About This Project**
> This project is being built as a **real-world SaaS platform**, while simultaneously serving as a comprehensive learning journey into advanced Spring Boot, Microservice/Modular Architecture, and modern backend development practices.
## Technology Stack & Architecture
*   **Backend Framework:** Spring Boot (Modular Monolith / Microservices Approach)
*   **Relational Database:** MySQL (for structured data like Users, Auth, Transactions)
*   **NoSQL Database:** MongoDB (for unstructured, flexible data like Chat history and rich Content/Blogs)
*   **Real-time Communication:** WebSockets / STOMP

---

## 🏗️ Master Plan: System Modules

The backend architecture is broken down into 7 high-level modules to ensure maximum scalability, clear separation of concerns, and distinct feature ownership.

### 1. `auth` (Security & Access Hub)
*   **Responsibility:** Authentication, Authorization, and Identity Verification.
*   **Features:**
    *   JWT Token Generation / Validation.
    *   Secure Login & Registration.
    *   Role-Based Access Control (RBAC) ensuring that `CLIENTS`, `LAWYERS`, and `ADMINS` only access what they are permitted to see.
    *   Social Login (Google, LinkedIn).

### 2. `user` (Identity & Profiles)
*   **Responsibility:** Storing and managing personal and professional data.
*   **Features:**
    *   **Client Management:** User preferences, billing history, saved resources.
    *   **Lawyer/Expert Profiles:** Practice areas, bar license number, biography, verified credentials.
    *   **Reviews & Ratings System:** Linking verified client feedback to professionals.

### 3. `chat` (Real-Time Communication)
*   **Responsibility:** Secure, instant messaging between clients and professionals.
*   **Features:**
    *   Live text messaging via WebSockets.
    *   Chat history routing to MongoDB for fast, schema-less retrieval.
    *   Secure file/attachment sharing capabilities.

### 4. `community` (Public Forums & Q&A)
*   **Responsibility:** Driving public engagement, creating a knowledge base, and generating SEO traffic.
*   **Features:**
    *   Structured Q&A board where users post legal questions.
    *   Verified lawyers provide answers, building trust.
    *   Upvote/downvote content prioritization.

### 5. `e-resource` (Digital Reading Library)
*   **Responsibility:** A vast content repository for educational materials and legal references.
*   **Features:**
    *   **Bare Acts & Statutes:** The written texts of various laws and legislations.
    *   **Case Laws:** Summaries and texts of historical/precedent-setting rulings.
    *   **Guides & Articles:** Educational content for everyday users.
    *   **Document Templates:** Downloadable, standardized legal documents (e.g., NDAs, Leases).

### 6. `consultation` (Booking & Scheduling)
*   **Responsibility:** Handling the logistics of hiring a lawyer officially.
*   **Features:**
    *   **Booking Engine:** Calendar integration for professionals to set available slots.
    *   **Payment Gateway:** Integrated billing (e.g., Razorpay) requiring payment to secure an appointment.
    *   **Reminders:** Automated notifications supporting Video/Audio/In-person meetings.

### 7. `ai-assistant` (First-Line Help & Summarization)
*   **Responsibility:** Automated triage, basic query handling, and document processing.
*   **Features:**
    *   **Chatbot Interface:** Capable of answering basic legal "how-to" questions instantly.
    *   **Contextual Handoff:** Suggests seamlessly booking a `consultation` with a verified professional if the query is complex.
    *   **AI Document Summarization:** Summarizes lengthy legal documents or contracts into plain English for clients.
