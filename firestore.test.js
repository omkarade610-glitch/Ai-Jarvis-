const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write user chats", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("chats").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("chats").doc("chat1").set({
      userId: ALICE_UID,
      title: "Test Chat",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create, read, update, and delete own chat", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const chatRef = aliceDb.collection("users").doc(ALICE_UID).collection("chats").doc("chat1");
  const now = new Date();

  await assertSucceeds(
    chatRef.set({
      userId: ALICE_UID,
      title: "JARVIS Discussion",
      lastMessage: "Hello JARVIS",
      messageCount: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(chatRef.get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("chats").get());

  await assertSucceeds(
    chatRef.update({
      title: "Updated Title",
      lastMessage: "How are you?",
      messageCount: 2,
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(chatRef.delete());
});

test("Cross-user access: Bob cannot read or write Alice's chats", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("chats").doc("chat1").set({
      userId: ALICE_UID,
      title: "Alice Secret Chat",
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("chats").doc("chat1").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("chats").doc("chat1").update({
      title: "Hacked Title",
      updatedAt: new Date(),
    })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("chats").doc("chat1").delete());
});

test("Knowledge item: owner can manage, non-owner rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection("users").doc(ALICE_UID).collection("knowledge").doc("doc1");
  const now = new Date();

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      title: "Quantum Physics 101",
      category: "Science",
      content: "Intro to wave functions.",
      createdAt: now,
      updatedAt: now,
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("knowledge").doc("doc1").get());
});

test("Projects & Memories: owner can create, non-owner rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj1").set({
      userId: ALICE_UID,
      name: "Weather App",
      type: "android",
      description: "Jetpack Compose app",
      status: "draft",
      codeSnippet: "println('hello')",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("memories").doc("mem1").set({
      userId: ALICE_UID,
      key: "preferred_language",
      value: "Hindi and English",
      category: "preference",
      isApproved: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj1").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("memories").doc("mem1").get());
});
