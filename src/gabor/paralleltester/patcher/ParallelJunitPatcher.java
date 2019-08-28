package gabor.paralleltester.patcher;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.JUnitPatcher;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.util.PathsList;
import com.intellij.util.containers.ContainerUtil;
import gabor.paralleltester.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ParallelJunitPatcher {

    public static void patchJavaParameters(@NotNull JavaParameters javaParameters) {
        ParametersList programParametersList = javaParameters.getProgramParametersList();

        List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

        programParametersList.clearAll();

        boolean classAdded = false;
        for (String s : list) {
            if (!s.contains("@")) {
                programParametersList.add(s);
            } else if (!classAdded) {
                classAdded = true;
                programParametersList.add(Resources.RUNNABLE_CLASS);
            }
        }

        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));
    }

}
