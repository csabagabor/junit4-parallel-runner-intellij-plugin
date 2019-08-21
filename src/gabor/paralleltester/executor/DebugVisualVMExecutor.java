package gabor.paralleltester.executor;

import com.intellij.execution.executors.DefaultDebugExecutor;
import gabor.paralleltester.Resources;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import com.intellij.junit4.JUnit4IdeaTestRunner;
import com.intellij.ExtensionPoints;
import com.intellij.execution.JUnitPatcher;
import org.jetbrains.idea.devkit.run.JUnitDevKitPatcher;
import org.jetbrains.idea.maven.*;
public class DebugVisualVMExecutor extends DefaultDebugExecutor {
	@NonNls
	public static final String EXECUTOR_ID = "Debug with VisualVM";
	public static final String DEBUG_WITH_VISUAL_VM = "DebugWithVisualVM";

	@NotNull
	public String getToolWindowId() {
		return getId();
	}

	public Icon getToolWindowIcon() {
		return Resources.DEBUG_13;
	}

	@NotNull
	public Icon getIcon() {
		return Resources.DEBUG;
	}

	public Icon getDisabledIcon() {
		return null;
	}

	public String getDescription() {
		return EXECUTOR_ID;
	}

	@NotNull
	public String getActionName() {
		return DEBUG_WITH_VISUAL_VM;
	}

	@NotNull
	public String getId() {
		return EXECUTOR_ID;
	}

	@NotNull
	public String getStartActionText() {
		return EXECUTOR_ID;
	}

	public String getContextActionId() {
		// HACK: ExecutorRegistryImpl expects this to be non-null, but we don't want any context actions for every file
		return getId() + " context-action-does-not-exist";
	}

	public String getHelpId() {
		return null;
	}

}
