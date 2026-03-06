@echo off
setlocal enabledelayedexpansion
:: check.bat -- Run the tests for a specific issue and report PASS / FAIL.
::
:: Usage (from project root, Command Prompt or PowerShell):
::   check.bat <issue-number>
::   check.bat 15
::   check.bat 24

set "REPO_ROOT=%~dp0"
if "%REPO_ROOT:~-1%"=="\" set "REPO_ROOT=%REPO_ROOT:~0,-1%"

:: -------------------------------------------------------------------------
:: Argument validation
:: -------------------------------------------------------------------------
if "%~1"=="" (
    echo Usage: check.bat ^<issue-number^>
    echo        check.bat 15
    exit /b 1
)

:: Strip leading zeros from issue number
set "ISSUE=%~1"
:strip_zeros
if "!ISSUE:~0,1!"=="0" (
    set "ISSUE=!ISSUE:~1!"
    if not "!ISSUE!"=="" goto :strip_zeros
)

:: -------------------------------------------------------------------------
:: Issue -> test filter mapping
:: -------------------------------------------------------------------------
set "TEST_CLASSES="

if "!ISSUE!"=="1"  set "TEST_CLASSES=com.taskflow.controller.TaskControllerTest#testCreateTaskReturns201"
if "!ISSUE!"=="2"  set "TEST_CLASSES=com.taskflow.controller.TaskControllerTest#testGetTaskByIdUsesCorrectPath"
if "!ISSUE!"=="3"  set "TEST_CLASSES=com.taskflow.controller.TaskControllerTest#testDeleteTaskReturns204"
if "!ISSUE!"=="4"  set "TEST_CLASSES=com.taskflow.controller.UserControllerTest#testRegisterEndpointUsesCorrectPath"
if "!ISSUE!"=="5"  set "TEST_CLASSES=com.taskflow.controller.UserControllerTest#testGetUserByIdReadsFromPath"
if "!ISSUE!"=="6"  set "TEST_CLASSES=com.taskflow.controller.ProjectControllerTest"
if "!ISSUE!"=="7"  set "TEST_CLASSES=com.taskflow.controller.CommentControllerTest"
if "!ISSUE!"=="8"  set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testCreateTaskSetsCreatedAt"
if "!ISSUE!"=="9"  set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testUpdateTaskDoesNotThrowForLargeId"
if "!ISSUE!"=="10" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testGetTasksByUserReturnsOnlyUserTasks"
if "!ISSUE!"=="11" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="12" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testCompleteTaskSetsStatusToCompleted"
if "!ISSUE!"=="13" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testGetPaginatedTasksDoesNotUseNegativePageIndex"
if "!ISSUE!"=="14" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testSearchTasksCaseInsensitive"
if "!ISSUE!"=="15" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testFindByUsernameQueriesUsernameField"
if "!ISSUE!"=="16" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testCreateUserDefaultRoleIsUser"
if "!ISSUE!"=="17" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testUpdateUserDoesNotOverwritePasswordWhenNull"
if "!ISSUE!"=="18" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testIsUsernameAvailableReturnsFalseWhenTaken+testIsUsernameAvailableReturnsTrueWhenFree"
if "!ISSUE!"=="19" set "TEST_CLASSES=com.taskflow.service.ProjectServiceTest#testGetProjectsByOwnerSortsByCreatedAtDesc"
if "!ISSUE!"=="20" set "TEST_CLASSES=com.taskflow.service.ProjectServiceTest#testAddMemberToProjectAddsCorrectUser"
if "!ISSUE!"=="21" set "TEST_CLASSES=com.taskflow.service.ProjectServiceTest#testGetTaskCountForProjectCountsOnlyProjectTasks"
if "!ISSUE!"=="22" set "TEST_CLASSES=com.taskflow.service.CommentServiceTest#testGetCommentsForTaskFiltersByTaskId"
if "!ISSUE!"=="23" set "TEST_CLASSES=com.taskflow.service.CommentServiceTest#testDeleteCommentEnforcesOwnership"
if "!ISSUE!"=="24" set "TEST_CLASSES=com.taskflow.mapper.TaskMapperTest#testToDtoMapsPriority+testToDtoMapsAllThreeFields"
if "!ISSUE!"=="25" set "TEST_CLASSES=com.taskflow.mapper.TaskMapperTest#testToDtoSetsDueDateCorrectly+testToDtoMapsAllThreeFields"
if "!ISSUE!"=="26" set "TEST_CLASSES=com.taskflow.mapper.UserMapperTest,com.taskflow.controller.UserControllerTest#testGetUserResponseDoesNotExposePassword"
if "!ISSUE!"=="27" set "TEST_CLASSES=com.taskflow.util.UtilsTest#testIsOverdueReturnsFalseForNow,com.taskflow.util.UtilsTest#testIsOverdueReturnsTrueForPast"
if "!ISSUE!"=="28" set "TEST_CLASSES=com.taskflow.util.UtilsTest#testGetDaysUntilDueReturnsZeroForOverdue,com.taskflow.util.UtilsTest#testGetDaysUntilDueReturnsPositiveForFuture"
if "!ISSUE!"=="29" set "TEST_CLASSES=com.taskflow.util.UtilsTest#testIsValidEmailRejectsNoTld,com.taskflow.util.UtilsTest#testIsValidEmailAcceptsValidEmail"
if "!ISSUE!"=="30" set "TEST_CLASSES=com.taskflow.util.UtilsTest#testIsValidPriorityAcceptsMedium,com.taskflow.util.UtilsTest#testIsValidPriorityAcceptsHighAndLow,com.taskflow.util.UtilsTest#testIsValidPriorityRejectsInvalid"
if "!ISSUE!"=="31" set "TEST_CLASSES=com.taskflow.exception.GlobalExceptionHandlerTest#testResourceNotFoundReturns404"
if "!ISSUE!"=="32" set "TEST_CLASSES=com.taskflow.exception.GlobalExceptionHandlerTest#testErrorResponseDoesNotLeakExceptionMessage"
if "!ISSUE!"=="33" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="34" set "TEST_CLASSES=com.taskflow.model.ModelTest#testTagEqualityForUnsavedInstances,com.taskflow.model.ModelTest#testTagEqualityByName"
if "!ISSUE!"=="35" set "TEST_CLASSES=com.taskflow.model.ModelTest#testTaskToStringDoesNotCauseStackOverflow"
if "!ISSUE!"=="36" set "TEST_CLASSES=com.taskflow.model.ModelTest#testProjectMemberCascadeDocumented"
if "!ISSUE!"=="37" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="38" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="40" set "TEST_CLASSES=com.taskflow.controller.TaskControllerTest#testGetTasksWithNegativePageReturns400"
if "!ISSUE!"=="41" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testGetTasksByStatusesUsesAllStatuses"
if "!ISSUE!"=="42" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testCreateUserHashesPasswordWithBCrypt"
if "!ISSUE!"=="43" set "TEST_CLASSES=com.taskflow.service.ProjectServiceTest#testRemoveMemberFromProjectPersistsChange"
if "!ISSUE!"=="44" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testGetOverdueTasksUsesCurrentTime"
if "!ISSUE!"=="45" set "TEST_CLASSES=com.taskflow.service.CommentServiceTest#testEditCommentSetsUpdatedAt"
if "!ISSUE!"=="46" set "TEST_CLASSES=com.taskflow.mapper.TaskMapperTest#testToDtoMapsProject,com.taskflow.mapper.TaskMapperTest#testToDtoMapsAllThreeFields"
if "!ISSUE!"=="47" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="48" set "TEST_CLASSES=com.taskflow.service.CommentServiceTest#testSendTaskAssignedNotificationDoesNotThrow"
if "!ISSUE!"=="49" set "TEST_CLASSES=com.taskflow.service.UserServiceTest#testGetPaginatedUsersAppliesSortParameter"
if "!ISSUE!"=="50" set "TEST_CLASSES=com.taskflow.service.ProjectServiceTest#testGetProjectStatsCalculatesPercentageCorrectly"
if "!ISSUE!"=="51" set "TEST_CLASSES=com.taskflow.service.TaskServiceTest#testBulkUpdateStatusIsTransactional"
if "!ISSUE!"=="53" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="54" set "TEST_CLASSES=NO_TEST"
if "!ISSUE!"=="55" set "TEST_CLASSES=NO_TEST"

if "!TEST_CLASSES!"=="" (
    echo [ERROR] Unknown issue number: !ISSUE!
    echo         Valid issues: 1-51 ^(excl. 11,33,37,38,47^), 53-55
    exit /b 1
)

:: -------------------------------------------------------------------------
:: No automated test
:: -------------------------------------------------------------------------
if "!TEST_CLASSES!"=="NO_TEST" (
    echo.
    echo   Issue #!ISSUE! does not have an automated unit test.
    echo   Verify your fix manually or ask your instructor for guidance.
    echo.
    exit /b 0
)

:: -------------------------------------------------------------------------
:: Build Maven -Dtest= filter
:: Replace '+' (same-class method separator) -- Maven accepts + natively
:: -------------------------------------------------------------------------
set "MVN_FILTER=!TEST_CLASSES!"

:: -------------------------------------------------------------------------
:: Detect mvn vs wrapper
:: -------------------------------------------------------------------------
set "MVN=mvn"
mvn --version >nul 2>&1
if errorlevel 1 (
    if exist "%REPO_ROOT%\mvnw.cmd" (
        set "MVN=%REPO_ROOT%\mvnw.cmd"
    ) else (
        echo [ERROR] Neither 'mvn' nor 'mvnw.cmd' found. Please install Maven or re-clone the repo.
        exit /b 1
    )
)

:: -------------------------------------------------------------------------
:: Run tests
:: -------------------------------------------------------------------------
echo.
echo [INFO]  Running tests for issue #!ISSUE!...
echo [INFO]  Filter: !MVN_FILTER!
echo.
cd /d "%REPO_ROOT%"
call "!MVN!" test "-Dtest=!MVN_FILTER!" --no-transfer-progress 2>&1
set "MVN_EXIT=!errorlevel!"

:: -------------------------------------------------------------------------
:: Result banner
:: -------------------------------------------------------------------------
if !MVN_EXIT! equ 0 (
    echo.
    echo +========================================+
    echo ^|  PASS -- Issue #!ISSUE! fixed!
    echo +========================================+
    echo.
    exit /b 0
) else (
    echo.
    echo +========================================+
    echo ^|  FAIL -- Issue #!ISSUE! not yet fixed
    echo +========================================+
    echo.
    echo   Review the test output above, then fix the bug in src\main\java\
    echo   and re-run:  check.bat !ISSUE!
    echo.
    exit /b 1
)
endlocal
