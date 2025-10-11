package com.chapman.edu.commissions.doubles.fake;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive demonstration of Test Doubles - Fake Pattern.
 *
 * FAKE OVERVIEW:
 * A Fake is a working implementation with real business logic, but simplified compared to
 * the production implementation. Fakes have actual behavior that makes them suitable for
 * testing, but they use shortcuts that make them unsuitable for production.
 *
 * KEY CHARACTERISTICS:
 * ┌─────────────────────────┬──────────────────────────────────────────┐
 * │ Characteristic          │ Description                              │
 * ├─────────────────────────┼──────────────────────────────────────────┤
 * │ Purpose                 │ Working implementation for testing       │
 * │ Behavior                │ Real business logic (simplified)         │
 * │ State                   │ Maintains state like real objects       │
 * │ Implementation          │ Functional but not production-ready      │
 * │ Complexity              │ More complex than stubs, simpler than real│
 * └─────────────────────────┴──────────────────────────────────────────┘
 *
 * FAKE vs OTHER TEST DOUBLES:
 *
 * Dummy    - Passed but never used (no behavior)
 * Stub     - Returns hardcoded values (predetermined responses)
 * Spy      - Wraps real object, records interactions
 * Mock     - Verifies behavior expectations
 * Fake     - Working implementation (simplified) ← YOU ARE HERE
 *
 * COMMON FAKE EXAMPLES:
 * • In-memory database instead of SQL database
 * • HashMap instead of Redis cache
 * • ArrayList instead of message queue
 * • Local file system instead of cloud storage
 * • Synchronized collections instead of distributed systems
 *
 * WHEN TO USE FAKES:
 * ✓ Testing with real behavior but avoiding external dependencies
 * ✓ Integration tests that need fast execution
 * ✓ Testing business logic without infrastructure
 * ✓ Development/testing when real services unavailable
 * ✓ Demonstrating API usage patterns
 *
 * WHEN NOT TO USE FAKES:
 * ✗ Production code (fakes are for testing only)
 * ✗ When simple stubs suffice (don't over-engineer)
 * ✗ Testing infrastructure-specific behavior
 * ✗ When you need to verify exact method calls (use mock)
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. In-Memory Repository - Database replacement
 * 2. Fake Caching - Simple cache implementation
 * 3. Fake Message Queue - Event processing
 * 4. Fake ID Generation - Sequence management
 * 5. Fake Notification Service - Email/SMS simulation
 * 6. Fake Search Engine - Query processing
 * 7. Fake Validation - Business rule checking
 * 8. Real-World Scenarios - Practical applications
 *
 */
@DisplayName("Test Doubles - Fake Pattern")
class FakeTest {

    // ============================================
    // FAKE IMPLEMENTATIONS
    // ============================================

    /**
     * FAKE REPOSITORY: In-memory implementation of a repository pattern.
     *
     * This fake acts like a real database but uses HashMap for storage.
     * Perfect for testing business logic without actual database.
     */
    static class FakeUserRepository {
        private final Map<String, User> storage = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        /**
         * Save a user (create or update)
         */
        public User save(User user) {
            if (user.getId() == null) {
                user.setId("USER-" + idGenerator.getAndIncrement());
            }
            storage.put(user.getId(), user);
            return user;
        }

        /**
         * Find user by ID
         */
        public Optional<User> findById(String id) {
            return Optional.ofNullable(storage.get(id));
        }

        /**
         * Find all users
         */
        public List<User> findAll() {
            return new ArrayList<>(storage.values());
        }

        /**
         * Find user by username
         */
        public Optional<User> findByUsername(String username) {
            return storage.values().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst();
        }

        /**
         * Find active users
         */
        public List<User> findActiveUsers() {
            return storage.values().stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
        }

        /**
         * Delete user by ID
         */
        public boolean deleteById(String id) {
            return storage.remove(id) != null;
        }

        /**
         * Count total users
         */
        public long count() {
            return storage.size();
        }

        /**
         * Check if user exists
         */
        public boolean existsById(String id) {
            return storage.containsKey(id);
        }

        /**
         * Clear all data (useful for test cleanup)
         */
        public void clear() {
            storage.clear();
        }
    }

    /**
     * FAKE DEAL REPOSITORY: In-memory deal storage with query capabilities.
     */
    static class FakeDealRepository {
        private final Map<String, Deal> storage = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        public Deal save(Deal deal) {
            if (deal.getId() == null) {
                deal.setId("DEAL-" + idGenerator.getAndIncrement());
            }
            storage.put(deal.getId(), deal);
            return deal;
        }

        public Optional<Deal> findById(String id) {
            return Optional.ofNullable(storage.get(id));
        }

        public List<Deal> findAll() {
            return new ArrayList<>(storage.values());
        }

        public List<Deal> findBySalesRep(String salesRepId) {
            return storage.values().stream()
                .filter(d -> salesRepId.equals(d.getSalesRepId()))
                .collect(Collectors.toList());
        }

        public List<Deal> findByStatus(DealStatus status) {
            return storage.values().stream()
                .filter(d -> status.equals(d.getStatus()))
                .collect(Collectors.toList());
        }

        public List<Deal> findByValueGreaterThan(BigDecimal minValue) {
            return storage.values().stream()
                .filter(d -> d.getValue() != null && d.getValue().compareTo(minValue) > 0)
                .collect(Collectors.toList());
        }

        public boolean deleteById(String id) {
            return storage.remove(id) != null;
        }

        public long count() {
            return storage.size();
        }

        public void clear() {
            storage.clear();
        }
    }

    /**
     * FAKE CACHE: Simple in-memory cache with TTL support.
     *
     * Simulates a real cache like Redis but uses HashMap.
     */
    static class FakeCache<K, V> {
        private final Map<K, CacheEntry<V>> storage = new ConcurrentHashMap<>();

        static class CacheEntry<V> {
            final V value;
            final long expiryTime;
            final boolean neverExpires;

            CacheEntry(V value, long ttlMillis) {
                this.value = value;
                this.neverExpires = (ttlMillis == Long.MAX_VALUE);
                if (neverExpires) {
                    this.expiryTime = Long.MAX_VALUE;
                } else {
                    this.expiryTime = System.currentTimeMillis() + ttlMillis;
                }
            }

            boolean isExpired() {
                return !neverExpires && System.currentTimeMillis() > expiryTime;
            }
        }

        public void put(K key, V value, long ttlMillis) {
            storage.put(key, new CacheEntry<>(value, ttlMillis));
        }

        public void put(K key, V value) {
            put(key, value, Long.MAX_VALUE); // Never expires
        }

        public Optional<V> get(K key) {
            CacheEntry<V> entry = storage.get(key);
            if (entry == null || entry.isExpired()) {
                storage.remove(key); // Clean up expired entry
                return Optional.empty();
            }
            return Optional.of(entry.value);
        }

        public void remove(K key) {
            storage.remove(key);
        }

        public void clear() {
            storage.clear();
        }

        public int size() {
            // Clean expired entries first
            storage.entrySet().removeIf(e -> e.getValue().isExpired());
            return storage.size();
        }

        public boolean containsKey(K key) {
            return get(key).isPresent();
        }
    }

    /**
     * FAKE NOTIFICATION SERVICE: Simulates email/SMS sending.
     *
     * Records notifications instead of actually sending them.
     */
    static class FakeNotificationService {
        private final List<Notification> sentNotifications = new ArrayList<>();

        static class Notification {
            final String recipient;
            final String subject;
            final String message;
            final NotificationType type;
            final LocalDate sentDate;

            Notification(String recipient, String subject, String message, NotificationType type) {
                this.recipient = recipient;
                this.subject = subject;
                this.message = message;
                this.type = type;
                this.sentDate = LocalDate.now();
            }
        }

        enum NotificationType {
            EMAIL, SMS, PUSH
        }

        public void sendEmail(String to, String subject, String message) {
            sentNotifications.add(new Notification(to, subject, message, NotificationType.EMAIL));
        }

        public void sendSms(String phoneNumber, String message) {
            sentNotifications.add(new Notification(phoneNumber, "SMS", message, NotificationType.SMS));
        }

        public void sendPush(String userId, String title, String message) {
            sentNotifications.add(new Notification(userId, title, message, NotificationType.PUSH));
        }

        public List<Notification> getSentNotifications() {
            return new ArrayList<>(sentNotifications);
        }

        public List<Notification> getNotificationsByRecipient(String recipient) {
            return sentNotifications.stream()
                .filter(n -> recipient.equals(n.recipient))
                .collect(Collectors.toList());
        }

        public List<Notification> getNotificationsByType(NotificationType type) {
            return sentNotifications.stream()
                .filter(n -> type.equals(n.type))
                .collect(Collectors.toList());
        }

        public void clear() {
            sentNotifications.clear();
        }

        public int getNotificationCount() {
            return sentNotifications.size();
        }
    }

    /**
     * FAKE EVENT BUS: Simple in-memory event publishing/subscription.
     *
     * Simulates message queue like RabbitMQ or Kafka.
     */
    static class FakeEventBus {
        private final Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();
        private final List<Event> publishedEvents = new ArrayList<>();

        static class Event {
            final String type;
            final Object payload;
            final LocalDate timestamp;

            Event(String type, Object payload) {
                this.type = type;
                this.payload = payload;
                this.timestamp = LocalDate.now();
            }
        }

        interface EventListener {
            void onEvent(Event event);
        }

        public void subscribe(String eventType, EventListener listener) {
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        }

        public void publish(String eventType, Object payload) {
            Event event = new Event(eventType, payload);
            publishedEvents.add(event);

            // Notify listeners
            List<EventListener> eventListeners = listeners.get(eventType);
            if (eventListeners != null) {
                eventListeners.forEach(listener -> listener.onEvent(event));
            }
        }

        public List<Event> getPublishedEvents() {
            return new ArrayList<>(publishedEvents);
        }

        public List<Event> getEventsByType(String eventType) {
            return publishedEvents.stream()
                .filter(e -> eventType.equals(e.type))
                .collect(Collectors.toList());
        }

        public void clear() {
            publishedEvents.clear();
            listeners.clear();
        }
    }

    /**
     * FAKE ID GENERATOR: Simple sequence-based ID generation.
     *
     * Simulates database sequence or UUID generation.
     */
    static class FakeIdGenerator {
        private final Map<String, AtomicLong> sequences = new ConcurrentHashMap<>();

        public String generateId(String prefix) {
            AtomicLong sequence = sequences.computeIfAbsent(prefix, k -> new AtomicLong(1));
            return prefix + "-" + sequence.getAndIncrement();
        }

        public long getCurrentSequence(String prefix) {
            return sequences.getOrDefault(prefix, new AtomicLong(0)).get();
        }

        public void reset(String prefix) {
            sequences.put(prefix, new AtomicLong(1));
        }

        public void resetAll() {
            sequences.clear();
        }
    }

    // ============================================
    // 1. BASIC FAKE USAGE
    // ============================================

    /**
     * BASIC FAKE REPOSITORY: Testing CRUD operations with in-memory storage.
     *
     * Demonstrates how fakes provide real behavior for testing.
     */
    @Test
    @DisplayName("Basic Fake - In-memory repository operations")
    void testBasicFakeRepository() {
        // ARRANGE: Create fake repository
        FakeUserRepository repository = new FakeUserRepository();

        // ACT: Perform CRUD operations
        User user = new User("jdoe", "john@example.com", "John", "Doe");
        User savedUser = repository.save(user);

        // ASSERT: Fake behaves like real repository
        assertNotNull(savedUser.getId());
        assertTrue(savedUser.getId().startsWith("USER-"));

        // Find by ID works
        Optional<User> found = repository.findById(savedUser.getId());
        assertTrue(found.isPresent());
        assertEquals("jdoe", found.get().getUsername());

        // Update works
        savedUser.setActive(false);
        repository.save(savedUser);
        User updated = repository.findById(savedUser.getId()).get();
        assertFalse(updated.isActive());

        // Delete works
        assertTrue(repository.deleteById(savedUser.getId()));
        assertFalse(repository.findById(savedUser.getId()).isPresent());
    }

    /**
     * FAKE REPOSITORY QUERIES: Testing complex queries with fake.
     *
     * Fakes can implement sophisticated query logic.
     */
    @Test
    @DisplayName("Fake Queries - Complex repository operations")
    void testFakeRepositoryQueries() {
        // ARRANGE: Setup test data
        FakeUserRepository repository = new FakeUserRepository();

        User activeUser1 = new User("active1", "active1@test.com", "Active", "One");
        activeUser1.setActive(true);

        User activeUser2 = new User("active2", "active2@test.com", "Active", "Two");
        activeUser2.setActive(true);

        User inactiveUser = new User("inactive", "inactive@test.com", "Inactive", "User");
        inactiveUser.setActive(false);

        repository.save(activeUser1);
        repository.save(activeUser2);
        repository.save(inactiveUser);

        // ACT & ASSERT: Test various queries
        List<User> allUsers = repository.findAll();
        assertEquals(3, allUsers.size());

        List<User> activeUsers = repository.findActiveUsers();
        assertEquals(2, activeUsers.size());

        Optional<User> byUsername = repository.findByUsername("active1");
        assertTrue(byUsername.isPresent());
        assertEquals("active1@test.com", byUsername.get().getEmail());

        assertEquals(3, repository.count());
    }

    // ============================================
    // 2. FAKE CACHE IMPLEMENTATION
    // ============================================

    /**
     * FAKE CACHE: Testing caching behavior with in-memory implementation.
     *
     * Simulates Redis/Memcached for testing cache-dependent code.
     */
    @Test
    @DisplayName("Fake Cache - In-memory caching with TTL")
    void testFakeCache() {
        // ARRANGE: Create fake cache
        FakeCache<String, String> cache = new FakeCache<>();

        // ACT & ASSERT: Basic cache operations
        cache.put("user:1", "cached-value");
        assertTrue(cache.containsKey("user:1"), "Cache should contain key user:1");

        Optional<String> cachedValue = cache.get("user:1");
        assertTrue(cachedValue.isPresent(), "Cached value should be present");
        assertEquals("cached-value", cachedValue.get());

        // Remove from cache
        cache.remove("user:1");
        assertFalse(cache.containsKey("user:1"));
    }

    /**
     * FAKE CACHE TTL: Testing cache expiration.
     *
     * Demonstrates time-based behavior in fakes.
     */
    @Test
    @DisplayName("Fake Cache - TTL expiration")
    void testFakeCacheTTL() throws InterruptedException {
        // ARRANGE: Create cache with short TTL
        FakeCache<String, String> cache = new FakeCache<>();

        // ACT: Put with 100ms TTL
        cache.put("short-lived", "value", 100);
        assertTrue(cache.containsKey("short-lived"));

        // Wait for expiration
        Thread.sleep(150);

        // ASSERT: Entry should be expired
        assertFalse(cache.containsKey("short-lived"));
        assertTrue(cache.get("short-lived").isEmpty());
    }

    // ============================================
    // 3. FAKE NOTIFICATION SERVICE
    // ============================================

    /**
     * FAKE NOTIFICATIONS: Testing notification logic without sending real emails.
     *
     * Records sent notifications for verification.
     */
    @Test
    @DisplayName("Fake Notifications - Recording sent messages")
    void testFakeNotificationService() {
        // ARRANGE: Create fake notification service
        FakeNotificationService notifications = new FakeNotificationService();

        // ACT: Send various notifications
        notifications.sendEmail("user@test.com", "Welcome", "Welcome to the system!");
        notifications.sendSms("+1234567890", "Your code is 1234");
        notifications.sendPush("USER-1", "New Message", "You have a new message");

        // ASSERT: Notifications were recorded
        assertEquals(3, notifications.getNotificationCount());

        List<FakeNotificationService.Notification> emails =
            notifications.getNotificationsByType(FakeNotificationService.NotificationType.EMAIL);
        assertEquals(1, emails.size());
        assertEquals("Welcome", emails.get(0).subject);

        List<FakeNotificationService.Notification> sms =
            notifications.getNotificationsByType(FakeNotificationService.NotificationType.SMS);
        assertEquals(1, sms.size());
        assertTrue(sms.get(0).message.contains("1234"));
    }

    /**
     * FAKE NOTIFICATIONS BY RECIPIENT: Testing recipient-specific queries.
     */
    @Test
    @DisplayName("Fake Notifications - Query by recipient")
    void testFakeNotificationsByRecipient() {
        // ARRANGE
        FakeNotificationService notifications = new FakeNotificationService();

        // ACT: Send multiple notifications to same recipient
        notifications.sendEmail("john@test.com", "Subject 1", "Message 1");
        notifications.sendEmail("john@test.com", "Subject 2", "Message 2");
        notifications.sendEmail("jane@test.com", "Subject 3", "Message 3");

        // ASSERT: Can query by recipient
        List<FakeNotificationService.Notification> johnNotifications =
            notifications.getNotificationsByRecipient("john@test.com");
        assertEquals(2, johnNotifications.size());

        List<FakeNotificationService.Notification> janeNotifications =
            notifications.getNotificationsByRecipient("jane@test.com");
        assertEquals(1, janeNotifications.size());
    }

    // ============================================
    // 4. FAKE EVENT BUS
    // ============================================

    /**
     * FAKE EVENT BUS: Testing event-driven architecture without real message queue.
     *
     * Simulates pub/sub messaging for testing.
     */
    @Test
    @DisplayName("Fake Event Bus - Publish/Subscribe pattern")
    void testFakeEventBus() {
        // ARRANGE: Create fake event bus
        FakeEventBus eventBus = new FakeEventBus();

        List<String> receivedEvents = new ArrayList<>();

        // Subscribe to events
        eventBus.subscribe("DealCreated", event -> {
            Deal deal = (Deal) event.payload;
            receivedEvents.add("Deal created: " + deal.getTitle());
        });

        // ACT: Publish events
        Deal deal = new Deal("Big Deal", new BigDecimal("100000"), "USER-1");
        eventBus.publish("DealCreated", deal);

        // ASSERT: Event was published and received
        assertEquals(1, eventBus.getPublishedEvents().size());
        assertEquals(1, receivedEvents.size());
        assertTrue(receivedEvents.get(0).contains("Big Deal"));
    }

    /**
     * FAKE EVENT BUS MULTIPLE SUBSCRIBERS: Testing multiple listeners.
     */
    @Test
    @DisplayName("Fake Event Bus - Multiple subscribers")
    void testFakeEventBusMultipleSubscribers() {
        // ARRANGE
        FakeEventBus eventBus = new FakeEventBus();

        List<String> subscriber1Events = new ArrayList<>();
        List<String> subscriber2Events = new ArrayList<>();

        eventBus.subscribe("UserCreated", e -> subscriber1Events.add("S1: " + e.type));
        eventBus.subscribe("UserCreated", e -> subscriber2Events.add("S2: " + e.type));

        // ACT: Publish event
        User user = new User("test", "test@test.com", "Test", "User");
        eventBus.publish("UserCreated", user);

        // ASSERT: Both subscribers received event
        assertEquals(1, subscriber1Events.size());
        assertEquals(1, subscriber2Events.size());
        assertEquals(1, eventBus.getEventsByType("UserCreated").size());
    }

    // ============================================
    // 5. FAKE ID GENERATOR
    // ============================================

    /**
     * FAKE ID GENERATOR: Testing ID generation without database sequences.
     *
     * Provides predictable IDs for testing.
     */
    @Test
    @DisplayName("Fake ID Generator - Sequence generation")
    void testFakeIdGenerator() {
        // ARRANGE
        FakeIdGenerator idGenerator = new FakeIdGenerator();

        // ACT: Generate IDs with different prefixes
        String userId1 = idGenerator.generateId("USER");
        String userId2 = idGenerator.generateId("USER");
        String dealId1 = idGenerator.generateId("DEAL");

        // ASSERT: IDs are sequential and prefixed correctly
        assertEquals("USER-1", userId1);
        assertEquals("USER-2", userId2);
        assertEquals("DEAL-1", dealId1);

        // Sequences are independent
        assertEquals(3, idGenerator.getCurrentSequence("USER"));  // Next value after 2
        assertEquals(2, idGenerator.getCurrentSequence("DEAL"));  // Next value after 1
    }

    /**
     * FAKE ID GENERATOR RESET: Testing sequence reset functionality.
     */
    @Test
    @DisplayName("Fake ID Generator - Sequence reset")
    void testFakeIdGeneratorReset() {
        // ARRANGE
        FakeIdGenerator idGenerator = new FakeIdGenerator();

        // Generate some IDs
        idGenerator.generateId("TEST");
        idGenerator.generateId("TEST");
        idGenerator.generateId("TEST");

        assertEquals(4, idGenerator.getCurrentSequence("TEST"));  // Next value after 3

        // ACT: Reset sequence
        idGenerator.reset("TEST");

        // ASSERT: Sequence restarted
        String nextId = idGenerator.generateId("TEST");
        assertEquals("TEST-1", nextId);
    }

    // ============================================
    // 6. REAL-WORLD SCENARIOS
    // ============================================

    /**
     * SCENARIO 1: Testing user registration workflow with fakes.
     *
     * Combines repository, notifications, and events.
     */
    @Test
    @DisplayName("Real-World - User registration workflow")
    void testUserRegistrationWorkflow() {
        // ARRANGE: Setup fake services
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeNotificationService notifications = new FakeNotificationService();
        FakeEventBus eventBus = new FakeEventBus();

        // Subscribe to user creation events
        eventBus.subscribe("UserRegistered", event -> {
            User user = (User) event.payload;
            notifications.sendEmail(
                user.getEmail(),
                "Welcome to Commission Calculator",
                "Welcome " + user.getFullName() + "!"
            );
        });

        // ACT: Register new user
        User newUser = new User("sarah", "sarah@test.com", "Sarah", "Johnson");
        User savedUser = userRepo.save(newUser);
        eventBus.publish("UserRegistered", savedUser);

        // ASSERT: User saved and notification sent
        assertTrue(userRepo.existsById(savedUser.getId()));
        assertEquals(1, notifications.getNotificationCount());

        FakeNotificationService.Notification welcomeEmail =
            notifications.getSentNotifications().get(0);
        assertEquals("sarah@test.com", welcomeEmail.recipient);
        assertTrue(welcomeEmail.message.contains("Sarah Johnson"));
    }

    /**
     * SCENARIO 2: Testing deal approval workflow.
     *
     * Complex business logic with multiple fake services.
     */
    @Test
    @DisplayName("Real-World - Deal approval workflow")
    void testDealApprovalWorkflow() {
        // ARRANGE: Setup fakes
        FakeDealRepository dealRepo = new FakeDealRepository();
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeNotificationService notifications = new FakeNotificationService();
        FakeEventBus eventBus = new FakeEventBus();

        // Create sales rep and manager
        User salesRep = new User("rep", "rep@test.com", "Sales", "Rep");
        salesRep.setManagerId("MGR-1");
        User manager = new User("mgr", "mgr@test.com", "Manager", "User");
        manager.setId("MGR-1");

        userRepo.save(salesRep);
        userRepo.save(manager);

        // Create deal needing approval
        Deal deal = new Deal("Large Deal", new BigDecimal("150000"), salesRep.getId());
        deal.setStatus(DealStatus.OPEN);
        dealRepo.save(deal);

        // Subscribe to deal events
        eventBus.subscribe("DealApproved", event -> {
            Deal approvedDeal = (Deal) event.payload;
            User rep = userRepo.findById(approvedDeal.getSalesRepId()).orElse(null);
            if (rep != null && rep.getManagerId() != null) {
                User mgr = userRepo.findById(rep.getManagerId()).orElse(null);
                if (mgr != null) {
                    notifications.sendEmail(
                        mgr.getEmail(),
                        "Deal Approved",
                        "Deal " + approvedDeal.getTitle() + " has been approved"
                    );
                }
            }
        });

        // ACT: Approve deal
        deal.setStatus(DealStatus.WON);
        dealRepo.save(deal);
        eventBus.publish("DealApproved", deal);

        // ASSERT: Deal approved and manager notified
        Deal approvedDeal = dealRepo.findById(deal.getId()).get();
        assertEquals(DealStatus.WON, approvedDeal.getStatus());
        assertEquals(1, notifications.getNotificationCount());
        assertEquals("mgr@test.com",
            notifications.getSentNotifications().get(0).recipient);
    }

    /**
     * SCENARIO 3: Testing caching layer in deal service.
     *
     * Demonstrates cache-aside pattern with fakes.
     */
    @Test
    @DisplayName("Real-World - Cache-aside pattern")
    void testCacheAsidePattern() {
        // ARRANGE: Setup repository and cache
        FakeDealRepository dealRepo = new FakeDealRepository();
        FakeCache<String, Deal> dealCache = new FakeCache<>();

        Deal deal = new Deal("Cached Deal", new BigDecimal("50000"), "USER-1");
        dealRepo.save(deal);

        // ACT: First access - cache miss
        String dealId = deal.getId();
        Optional<Deal> cachedDeal = dealCache.get(dealId);
        if (cachedDeal.isEmpty()) {
            Optional<Deal> fromDb = dealRepo.findById(dealId);
            fromDb.ifPresent(d -> dealCache.put(dealId, d));
        }

        // Second access - cache hit
        Optional<Deal> secondAccess = dealCache.get(dealId);

        // ASSERT: Cache populated after first access
        assertTrue(secondAccess.isPresent());
        assertEquals("Cached Deal", secondAccess.get().getTitle());

        // Verify cache working
        dealCache.remove(dealId);
        assertFalse(dealCache.containsKey(dealId));
    }

    /**
     * SCENARIO 4: Testing pagination with fake repository.
     *
     * Demonstrates complex query logic in fakes.
     */
    @Test
    @DisplayName("Real-World - Repository pagination")
    void testRepositoryPagination() {
        // ARRANGE: Create repository with test data
        FakeDealRepository dealRepo = new FakeDealRepository();

        // Create 25 deals
        for (int i = 1; i <= 25; i++) {
            Deal deal = new Deal("Deal " + i, new BigDecimal(i * 1000), "USER-1");
            dealRepo.save(deal);
        }

        // ACT: Implement pagination logic
        int pageSize = 10;
        List<Deal> allDeals = dealRepo.findAll();
        int totalPages = (int) Math.ceil((double) allDeals.size() / pageSize);

        List<Deal> page1 = allDeals.subList(0, Math.min(pageSize, allDeals.size()));
        List<Deal> page2 = allDeals.subList(pageSize, Math.min(2 * pageSize, allDeals.size()));
        List<Deal> page3 = allDeals.subList(2 * pageSize, allDeals.size());

        // ASSERT: Pagination works correctly
        assertEquals(25, allDeals.size());
        assertEquals(3, totalPages);
        assertEquals(10, page1.size());
        assertEquals(10, page2.size());
        assertEquals(5, page3.size());
    }

    /**
     * SCENARIO 5: Testing batch processing with events.
     *
     * Simulates async processing with fake event bus.
     */
    @Test
    @DisplayName("Real-World - Batch event processing")
    void testBatchEventProcessing() {
        // ARRANGE: Setup event bus and processor
        FakeEventBus eventBus = new FakeEventBus();
        List<Deal> processedDeals = new ArrayList<>();

        eventBus.subscribe("DealBatchImported", event -> {
            @SuppressWarnings("unchecked")
            List<Deal> deals = (List<Deal>) event.payload;
            processedDeals.addAll(deals);
        });

        // ACT: Import batch of deals
        List<Deal> batchDeals = Arrays.asList(
            new Deal("Batch 1", new BigDecimal("1000"), "USER-1"),
            new Deal("Batch 2", new BigDecimal("2000"), "USER-2"),
            new Deal("Batch 3", new BigDecimal("3000"), "USER-3")
        );

        eventBus.publish("DealBatchImported", batchDeals);

        // ASSERT: All deals processed
        assertEquals(3, processedDeals.size());
        assertEquals("Batch 1", processedDeals.get(0).getTitle());
    }

    // ============================================
    // 7. FAKE BEST PRACTICES
    // ============================================

    /**
     * BEST PRACTICE 1: Fakes should be simple but functional.
     *
     * Don't over-engineer fakes - keep them focused.
     */
    @Test
    @DisplayName("Best Practice - Simple but functional fakes")
    void testSimpleFunctionality() {
        // GOOD: Simple fake with essential features
        FakeUserRepository simpleRepo = new FakeUserRepository();

        User user = new User("test", "test@test.com", "Test", "User");
        simpleRepo.save(user);

        // Essential operations work
        assertTrue(simpleRepo.findById(user.getId()).isPresent());
        assertEquals(1, simpleRepo.count());

        // Don't need all features of production repository
        // Focus on what tests need
    }

    /**
     * BEST PRACTICE 2: Fakes should maintain state correctly.
     *
     * State management is key to fake reliability.
     */
    @Test
    @DisplayName("Best Practice - Consistent state management")
    void testConsistentStateManagement() {
        // ARRANGE
        FakeDealRepository repository = new FakeDealRepository();

        // ACT: Perform state-changing operations
        Deal deal = new Deal("State Test", new BigDecimal("1000"), "USER-1");
        repository.save(deal);

        deal.setTitle("Updated Title");
        repository.save(deal);

        // ASSERT: State persists correctly
        Deal retrieved = repository.findById(deal.getId()).get();
        assertEquals("Updated Title", retrieved.getTitle());
    }

    /**
     * BEST PRACTICE 3: Provide clear() method for test isolation.
     *
     * Fakes should be easily resettable between tests.
     */
    @Test
    @DisplayName("Best Practice - Test isolation with clear()")
    void testTestIsolation() {
        // ARRANGE
        FakeUserRepository repository = new FakeUserRepository();

        // Add data
        repository.save(new User("user1", "user1@test.com", "User", "One"));
        repository.save(new User("user2", "user2@test.com", "User", "Two"));
        assertEquals(2, repository.count());

        // ACT: Clear for next test
        repository.clear();

        // ASSERT: Clean state
        assertEquals(0, repository.count());
    }

    /**
     * BEST PRACTICE 4: Fakes should throw appropriate exceptions.
     *
     * Mimic real behavior including error cases.
     */
    @Test
    @DisplayName("Best Practice - Appropriate exception handling")
    void testAppropriateExceptions() {
        // Fake should handle edge cases like real implementation
        FakeCache<String, String> cache = new FakeCache<>();

        // Getting non-existent key returns empty
        assertTrue(cache.get("nonexistent").isEmpty());

        // Not throwing exception is appropriate for cache
        // Real implementation would behave similarly
    }

    // ============================================
    // 8. COMMON PITFALLS
    // ============================================

    /**
     * PITFALL 1: Making fakes too complex.
     *
     * Fakes should be simpler than production code.
     */
    @Test
    @DisplayName("Pitfall - Over-engineered fake")
    void testAvoidOverEngineering() {
        // WRONG: Fake with too many features becomes maintenance burden
        // Don't add features "just in case"

        // RIGHT: Simple fake with only needed features
        FakeUserRepository simpleRepo = new FakeUserRepository();
        // Has basic CRUD, that's enough for most tests

        User user = new User("simple", "simple@test.com", "Simple", "User");
        simpleRepo.save(user);

        assertTrue(simpleRepo.findById(user.getId()).isPresent());
        // Simple and effective
    }

    /**
     * PITFALL 2: Using fakes in production code.
     *
     * Fakes are ONLY for testing.
     */
    @Test
    @DisplayName("Pitfall - Fakes are for testing only")
    void testFakesForTestingOnly() {
        // IMPORTANT: Never use fakes in production

        // TESTING: ✓ Use fakes
        FakeUserRepository testRepo = new FakeUserRepository();
        User user = new User("test", "test@test.com", "Test", "User");
        testRepo.save(user);

        // PRODUCTION: ✗ Use real implementation
        // ProductionUserRepository prodRepo = new ProductionUserRepository();

        assertNotNull(testRepo);
        // Fakes provide fast, reliable testing
        // Production needs real database, transactions, etc.
    }

    /**
     * PITFALL 3: Not maintaining fake consistency with real implementation.
     *
     * Fakes should mirror real behavior closely.
     */
    @Test
    @DisplayName("Pitfall - Keep fakes consistent with real")
    void testMaintainConsistency() {
        // Fake should behave like real implementation
        FakeDealRepository fakeRepo = new FakeDealRepository();

        Deal deal = new Deal("Test", new BigDecimal("1000"), "USER-1");

        // Save should return saved entity (like real JPA repository)
        Deal saved = fakeRepo.save(deal);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        // Subsequent saves should update, not create new
        String originalId = saved.getId();
        saved.setTitle("Updated");
        Deal updated = fakeRepo.save(saved);
        assertEquals(originalId, updated.getId());
    }
}