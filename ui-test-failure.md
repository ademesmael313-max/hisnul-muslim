# UI smoke test report

commit: 5a749962b484bd73b55445ef50e1b881ba4e62c3

## gradle (tail)
```
ute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:303)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:463)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:380)
	at org.gradle.execution.plan.DefaultPlanExecutor.process(DefaultPlanExecutor.java:116)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph.executeWithServices(DefaultTaskExecutionGraph.java:138)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph.execute(DefaultTaskExecutionGraph.java:123)
	at org.gradle.execution.SelectedTaskExecutionAction.execute(SelectedTaskExecutionAction.java:35)
	at org.gradle.execution.DryRunBuildExecutionAction.execute(DryRunBuildExecutionAction.java:51)
	at org.gradle.execution.BuildOperationFiringBuildWorkerExecutor$ExecuteTasks.call(BuildOperationFiringBuildWorkerExecutor.java:54)
	at org.gradle.execution.BuildOperationFiringBuildWorkerExecutor$ExecuteTasks.call(BuildOperationFiringBuildWorkerExecutor.java:43)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:200)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:195)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.execution.BuildOperationFiringBuildWorkerExecutor.execute(BuildOperationFiringBuildWorkerExecutor.java:40)
	at org.gradle.internal.build.DefaultBuildLifecycleController.lambda$executeTasks$10(DefaultBuildLifecycleController.java:313)
	at org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:266)
	at org.gradle.internal.model.StateTransitionController.lambda$tryTransition$8(StateTransitionController.java:177)
	at org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:44)
	at org.gradle.internal.model.StateTransitionController.tryTransition(StateTransitionController.java:177)
	at org.gradle.internal.build.DefaultBuildLifecycleController.executeTasks(DefaultBuildLifecycleController.java:304)
	at org.gradle.internal.build.DefaultBuildWorkGraphController$DefaultBuildWorkGraph.runWork(DefaultBuildWorkGraphController.java:220)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withLocks(DefaultWorkerLeaseService.java:264)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:128)
	at org.gradle.composite.internal.DefaultBuildController.doRun(DefaultBuildController.java:181)
	at org.gradle.composite.internal.DefaultBuildController.access$000(DefaultBuildController.java:50)
	at org.gradle.composite.internal.DefaultBuildController$BuildOpRunnable.lambda$run$0(DefaultBuildController.java:198)
	at org.gradle.internal.operations.CurrentBuildOperationRef.with(CurrentBuildOperationRef.java:80)
	at org.gradle.composite.internal.DefaultBuildController$BuildOpRunnable.run(DefaultBuildController.java:198)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:47)


BUILD FAILED in 4m 6s
69 actionable tasks: 64 executed, 5 from cache
```
## test results
```
app/build/outputs/androidTest-results/connected/debug/TEST-emulator-5554 - 11-_app-.xml
<?xml version='1.0' encoding='UTF-8' ?>
<testsuite name="com.hisnulmuslim.amharic.AppSmokeTest" tests="5" failures="2" errors="0" skipped="0" time="49.925" timestamp="2026-11-12T06:45:31" hostname="localhost">
  <properties>
    <property name="device" value="emulator-5554 - 11" />
    <property name="flavor" value="" />
    <property name="project" value=":app" />
  </properties>
  <testcase name="homeScreenRendersBook" classname="com.hisnulmuslim.amharic.AppSmokeTest" time="1.894" />
  <testcase name="searchFindsDuas" classname="com.hisnulmuslim.amharic.AppSmokeTest" time="2.468" />
  <testcase name="tasbihScreenOpens" classname="com.hisnulmuslim.amharic.AppSmokeTest" time="1.53">
    <failure>java.lang.AssertionError: Failed to inject touch input.
Reason: Expected exactly '1' node but could not find any node that satisfies: (Text + EditableText contains 'ተስቢሕ ቆጣሪ' (ignoreCase: false))
However, the unmerged tree contains '1' node that matches. Are you missing `useUnmergedNode = true` in your finder?

at androidx.compose.ui.test.SemanticsNodeInteraction.fetchOneOrDie(SemanticsNodeInteraction.kt:178)
at androidx.compose.ui.test.SemanticsNodeInteraction.fetchOneOrDie$default(SemanticsNodeInteraction.kt:150)
at androidx.compose.ui.test.SemanticsNodeInteraction.fetchSemanticsNode(SemanticsNodeInteraction.kt:84)
at androidx.compose.ui.test.ActionsKt.performTouchInput(Actions.kt:368)
at androidx.compose.ui.test.Actions_androidKt.performClickImpl(Actions.android.kt:20)
at androidx.compose.ui.test.ActionsKt.performClick(Actions.kt:51)
at com.hisnulmuslim.amharic.AppSmokeTest.tasbihScreenOpens(AppSmokeTest.kt:65)
</failure>
  </testcase>
  <testcase name="openChapterFromDuaOfTheDay" classname="com.hisnulmuslim.amharic.AppSmokeTest" time="41.235">
    <failure>androidx.compose.ui.test.ComposeTimeoutException: Condition still not satisfied after 40000 ms
at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$AndroidComposeUiTestImpl.waitUntil(ComposeUiTest.android.kt:431)
at androidx.compose.ui.test.junit4.AndroidComposeTestRule.waitUntil(AndroidComposeTestRule.android.kt:306)
at com.hisnulmuslim.amharic.AppSmokeTest.openChapterFromDuaOfTheDay(AppSmokeTest.kt:45)
</failure>
  </testcase>
  <testcase name="bookmarkADua" classname="com.hisnulmuslim.amharic.AppSmokeTest" time="1.843" />
</testsuite>```
## logcat (filtered)
```
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.rules.ExternalResource$1.evaluate(ExternalResource.java:54)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.AndroidComposeTestRule$apply$1$evaluate$1.invoke(AndroidComposeTestRule.android.kt:272)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.AndroidComposeTestRule$apply$1$evaluate$1.invoke(AndroidComposeTestRule.android.kt:271)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$AndroidComposeUiTestImpl.withDisposableContent(ComposeUiTest.android.kt:495)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1$1$1$1$1$1.invoke(ComposeUiTest.android.kt:327)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.withComposeIdlingResource(ComposeUiTest.android.kt:379)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.access$withComposeIdlingResource(ComposeUiTest.android.kt:230)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1$1$1$1$1.invoke(ComposeUiTest.android.kt:326)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.withWindowRecomposer(ComposeUiTest.android.kt:353)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.access$withWindowRecomposer(ComposeUiTest.android.kt:230)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1$1$1$1.invoke(ComposeUiTest.android.kt:325)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.withTestCoroutines(ComposeUiTest.android.kt:366)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.access$withTestCoroutines(ComposeUiTest.android.kt:230)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1$1$1.invoke(ComposeUiTest.android.kt:324)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.EspressoLink.withStrategy(EspressoLink.android.kt:66)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1$1.invoke(ComposeUiTest.android.kt:323)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.IdlingResourceRegistry.withRegistry(IdlingResourceRegistry.jvm.kt:157)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment$runTest$1.invoke(ComposeUiTest.android.kt:322)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.ComposeRootRegistry.withRegistry(ComposeRootRegistry.android.kt:146)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.AndroidComposeUiTestEnvironment.runTest(ComposeUiTest.android.kt:321)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at androidx.compose.ui.test.junit4.AndroidComposeTestRule$apply$1.evaluate(AndroidComposeTestRule.android.kt:271)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
11-12 06:45:28.268  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at androidx.test.ext.junit.runners.AndroidJUnit4.run(AndroidJUnit4.java:162)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.Suite.runChild(Suite.java:128)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.Suite.runChild(Suite.java:27)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at org.junit.runner.JUnitCore.run(JUnitCore.java:115)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at androidx.test.internal.runner.TestExecutor.execute(TestExecutor.java:68)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at androidx.test.internal.runner.TestExecutor.execute(TestExecutor.java:59)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at androidx.test.runner.AndroidJUnitRunner.onStart(AndroidJUnitRunner.java:463)
11-12 06:45:28.269  2407  2427 E TestRunner: 	at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:2205)
11-12 06:45:28.269  2407  2427 E TestRunner: ----- end exception -----
11-12 06:45:28.269  2407  2427 I TestRunner: finished: openChapterFromDuaOfTheDay(com.hisnulmuslim.amharic.AppSmokeTest)
11-12 06:45:28.275  2407  2427 I TestRunner: started: bookmarkADua(com.hisnulmuslim.amharic.AppSmokeTest)
11-12 06:45:28.280   505   643 I ActivityTaskManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10008000 cmp=com.hisnulmuslim.amharic.debug/com.hisnulmuslim.amharic.MainActivity} from uid 10130
11-12 06:45:28.712  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: PRE_ON_CREATE
11-12 06:45:28.725  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: CREATED
11-12 06:45:28.725  2407  2407 V ActivityScenario: Update currentActivityStage to CREATED, currentActivity=com.hisnulmuslim.amharic.MainActivity@af660a3
11-12 06:45:28.725  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: STARTED
11-12 06:45:28.726  2407  2407 V ActivityScenario: Update currentActivityStage to STARTED, currentActivity=com.hisnulmuslim.amharic.MainActivity@af660a3
11-12 06:45:28.726  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: RESUMED
11-12 06:45:28.726  2407  2407 V ActivityScenario: Update currentActivityStage to RESUMED, currentActivity=com.hisnulmuslim.amharic.MainActivity@af660a3
11-12 06:45:29.152   505   543 D ArtManagerInternalImpl: /data/misc/iorapd/com.hisnulmuslim.amharic.debug/1/com.hisnulmuslim.amharic.MainActivity/compiled_traces/compiled_trace.pb doesn't exist
11-12 06:45:29.153   505   543 I ActivityTaskManager: Displayed com.hisnulmuslim.amharic.debug/com.hisnulmuslim.amharic.MainActivity: +862ms
11-12 06:45:29.624   505  2504 I ActivityTaskManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10000000 cmp=com.hisnulmuslim.amharic.debug.test/androidx.test.core.app.InstrumentationActivityInvoker$EmptyActivity} from uid 10130
11-12 06:45:29.652  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: PAUSED
11-12 06:45:29.652  2407  2407 V ActivityScenario: Update currentActivityStage to PAUSED, currentActivity=com.hisnulmuslim.amharic.MainActivity@af660a3
11-12 06:45:29.942  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: STOPPED
11-12 06:45:29.942  2407  2407 V ActivityScenario: Update currentActivityStage to STOPPED, currentActivity=com.hisnulmuslim.amharic.MainActivity@af660a3
11-12 06:45:30.033  2407  2407 D LifecycleMonitor: Lifecycle status change: com.hisnulmuslim.amharic.MainActivity@af660a3 in: DESTROYED
11-12 06:45:30.083  2407  2427 I TestRunner: finished: bookmarkADua(com.hisnulmuslim.amharic.AppSmokeTest)
11-12 06:45:30.139  2407  2427 I TestRunner: run finished: 5 tests, 2 failed, 0 ignored
11-12 06:45:30.144   505   643 I ActivityManager: Force stopping com.hisnulmuslim.amharic.debug appid=10130 user=0: finished inst
11-12 06:45:30.144   505   643 I ActivityManager: Killing 2407:com.hisnulmuslim.amharic.debug/u0a130 (adj 0): stop com.hisnulmuslim.amharic.debug due to finished inst
11-12 06:45:30.160  2395  2395 D AndroidRuntime: Shutting down VM
11-12 06:45:30.863   505   544 I ActivityManager: Force stopping com.hisnulmuslim.amharic.debug appid=10130 user=-1: deletePackageX
11-12 06:45:30.866   505   561 I PackageManager: Removing permission com.hisnulmuslim.amharic.debug.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION that used to be declared by com.hisnulmuslim.amharic.debug
11-12 06:45:30.866   505   561 W PackageManager: Removing dangling permission: com.hisnulmuslim.amharic.debug.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION from package com.hisnulmuslim.amharic.debug
11-12 06:45:30.900   505   561 I ActivityManager: Force stopping com.hisnulmuslim.amharic.debug appid=10130 user=0: pkg removed
11-12 06:45:30.903   505   610 V StorageManagerService: Package com.hisnulmuslim.amharic.debug does not have legacy storage
11-12 06:45:30.903  1446  1446 I MediaProvider: Invalidating LocalCallingIdentity cache for package com.hisnulmuslim.amharic.debug. Reason: package android.intent.action.PACKAGE_REMOVED
11-12 06:45:30.905   505   535 E PackageManager: failed to find package com.hisnulmuslim.amharic.debug
11-12 06:45:30.906   505   544 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.intent.action.PACKAGE_REMOVED dat=package:com.hisnulmuslim.amharic.debug flg=0x4000010 (has extras) } to com.android.gallery3d/.app.PackagesMonitor
11-12 06:45:30.906   505   544 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.intent.action.PACKAGE_REMOVED dat=package:com.hisnulmuslim.amharic.debug flg=0x4000010 (has extras) } to com.android.musicfx/.Compatibility$Receiver
11-12 06:45:30.906   505   535 E OverlayManager: Failed to change enabled overlays for com.hisnulmuslim.amharic.debug user 0
11-12 06:45:30.930   505   505 W WifiService: Couldn't get PackageInfo for package:com.hisnulmuslim.amharic.debug
11-12 06:45:30.930   505   505 D WifiService: Remove settings for package:com.hisnulmuslim.amharic.debug
11-12 06:45:30.931   505   505 I ConditionProviders: Disallowing condition provider com.hisnulmuslim.amharic.debug
11-12 06:45:30.931   505   650 D WifiConfigManager: Remove all networks for app ApplicationInfo{f034832 com.hisnulmuslim.amharic.debug}
11-12 06:45:30.936   869   869 D Launcher.Model: package removed received com.hisnulmuslim.amharic.debug
11-12 06:45:30.936   869   935 D PackageUpdatedTask: Removing app iconcom.hisnulmuslim.amharic.debug
11-12 06:45:30.937   505   650 I PasspointManager: No app ops listener found for com.hisnulmuslim.amharic.debug
11-12 06:45:30.940   505   591 I RollbackManager: broadcast=ACTION_PACKAGE_FULLY_REMOVED pkg=com.hisnulmuslim.amharic.debug
11-12 06:45:30.941   505   505 W WifiService: Couldn't get PackageInfo for package:com.hisnulmuslim.amharic.debug
11-12 06:45:30.941   505   505 D WifiService: Remove settings for package:com.hisnulmuslim.amharic.debug
11-12 06:45:30.941   505   650 D WifiConfigManager: Remove all networks for app ApplicationInfo{997ab8a com.hisnulmuslim.amharic.debug}
11-12 06:45:30.945   505   650 I PasspointManager: No app ops listener found for com.hisnulmuslim.amharic.debug
11-12 06:45:30.964   505   544 I ActivityManager: Force stopping com.hisnulmuslim.amharic.debug.test appid=10131 user=-1: deletePackageX
11-12 06:45:30.964   505   544 I ActivityManager: Killing 2449:com.hisnulmuslim.amharic.debug.test/u0a131 (adj 900): stop com.hisnulmuslim.amharic.debug.test due to deletePackageX
11-12 06:45:31.047   505   561 I ActivityManager: Force stopping com.hisnulmuslim.amharic.debug.test appid=10131 user=0: pkg removed
11-12 06:45:31.050   505   610 V StorageManagerService: Package com.hisnulmuslim.amharic.debug.test does not have legacy storage
11-12 06:45:31.052   505   535 E PackageManager: failed to find package com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.052   505   535 E OverlayManager: Failed to change enabled overlays for com.hisnulmuslim.amharic.debug.test user 0
11-12 06:45:31.053  1446  1446 I MediaProvider: Invalidating LocalCallingIdentity cache for package com.hisnulmuslim.amharic.debug.test. Reason: package android.intent.action.PACKAGE_REMOVED
11-12 06:45:31.058   505   505 W WifiService: Couldn't get PackageInfo for package:com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.058   505   505 D WifiService: Remove settings for package:com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.058   505   650 D WifiConfigManager: Remove all networks for app ApplicationInfo{980e745 com.hisnulmuslim.amharic.debug.test}
11-12 06:45:31.059   505   505 I ConditionProviders: Disallowing condition provider com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.066   505   591 I RollbackManager: broadcast=ACTION_PACKAGE_FULLY_REMOVED pkg=com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.067   505   505 W WifiService: Couldn't get PackageInfo for package:com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.067   505   505 D WifiService: Remove settings for package:com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.071   505   650 I PasspointManager: No app ops listener found for com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.071   505   650 D WifiConfigManager: Remove all networks for app ApplicationInfo{de1ec84 com.hisnulmuslim.amharic.debug.test}
11-12 06:45:31.072   869   869 D Launcher.Model: package removed received com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.072   869   935 D PackageUpdatedTask: Removing app iconcom.hisnulmuslim.amharic.debug.test
11-12 06:45:31.076   505   650 I PasspointManager: No app ops listener found for com.hisnulmuslim.amharic.debug.test
11-12 06:45:31.189  1446  1895 I MediaProvider: Begin Intent { act=android.intent.action.PACKAGE_FULLY_REMOVED dat=package:com.hisnulmuslim.amharic.debug flg=0x5000010 cmp=com.android.providers.media.module/com.android.providers.media.MediaService (has extras) }
11-12 06:45:31.189  1446  1895 I MediaProvider: End Intent { act=android.intent.action.PACKAGE_FULLY_REMOVED dat=package:com.hisnulmuslim.amharic.debug flg=0x5000010 cmp=com.android.providers.media.module/com.android.providers.media.MediaService (has extras) }
11-12 06:45:31.192   505   780 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.intent.action.PACKAGE_REMOVED dat=package:com.hisnulmuslim.amharic.debug.test flg=0x4000010 (has extras) } to com.android.gallery3d/.app.PackagesMonitor
11-12 06:45:31.194   505   544 W BroadcastQueue: Background execution not allowed: receiving Intent { act=android.intent.action.PACKAGE_REMOVED dat=package:com.hisnulmuslim.amharic.debug.test flg=0x4000010 (has extras) } to com.android.musicfx/.Compatibility$Receiver
11-12 06:45:31.199  1446  1895 I MediaProvider: Begin Intent { act=android.intent.action.PACKAGE_FULLY_REMOVED dat=package:com.hisnulmuslim.amharic.debug.test flg=0x5000010 cmp=com.android.providers.media.module/com.android.providers.media.MediaService (has extras) }
11-12 06:45:31.199  1446  1895 I MediaProvider: End Intent { act=android.intent.action.PACKAGE_FULLY_REMOVED dat=package:com.hisnulmuslim.amharic.debug.test flg=0x5000010 cmp=com.android.providers.media.module/com.android.providers.media.MediaService (has extras) }
```
