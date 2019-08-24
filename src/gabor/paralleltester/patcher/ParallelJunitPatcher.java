package gabor.paralleltester.patcher;

import com.intellij.execution.JUnitPatcher;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.util.PathsList;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParallelJunitPatcher extends JUnitPatcher {
    @Override
    public void patchJavaParameters(@Nullable Module module, @NotNull JavaParameters javaParameters) {
        System.out.println();
        PluginManager.getPlugins();
    }

}
