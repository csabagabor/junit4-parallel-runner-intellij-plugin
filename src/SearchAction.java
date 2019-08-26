import com.intellij.analysis.AnalysisScope;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.testframework.TestSearchScope;
import com.intellij.ide.BrowserUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiRecursiveElementVisitor;

import java.util.HashSet;
import java.util.Set;

public class SearchAction extends AnAction {
    /**
     * Convert selected text to a URL friendly string.
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();

        // For searches from the editor, we should also get file type information
        // to help add scope to the search using the Stack overflow search syntax.
        //
        // https://stackoverflow.com/help/searching

        //
        DataContext dataContext = e.getDataContext();
        final Project project = DataKeys.PROJECT.getData(dataContext);
        final Module module = DataKeys.MODULE.getData(dataContext);

        final Set<String> packageNameSet = new HashSet<String>();

        AnalysisScope moduleScope = new AnalysisScope(module);
        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                    final PsiPackage aPackage =
                            JavaPsiFacade.getInstance(project).findPackage(psiJavaFile.getPackageName());
                    if (aPackage != null) {
                        packageNameSet.add(aPackage.getQualifiedName());
                    }
                }
            }
        });

//        VirtualFile instance = LocalFileSystem.getInstance()
//                .findFileByPath("C:\\Users\\admin\\Documents\\GitHub\\ParallelJunitTester-intellij-plugin\\src\\A.java");


        VirtualFile instance = LocalFileSystem.getInstance()
                .findFileByPath("C:\\Users\\admin\\Documents\\GitHub\\plugin\\test2\\src\\main\\java\\A.java");


//        CompilerManager.getInstance(project).compile(new VirtualFile[]{instance}, new CompileStatusNotification() {
//            @Override
//            public void finished(boolean b, int i, int i1, CompileContext compileContext) {
//                System.out.println("ready");
//            }
//        });
//
//
//        String allPackageNames = "";
//        for (String packageName : packageNameSet) {
//            allPackageNames = allPackageNames + packageName + "\n";
//        }
//        //
//
//
//        String languageTag = "";
//        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
//        if (file != null) {
//            Language lang = e.getData(CommonDataKeys.PSI_FILE).getLanguage();
//            languageTag = "+[" + lang.getDisplayName().toLowerCase() + "]";
//        }



        PsiFile testFile = e.getData(LangDataKeys.PSI_FILE);
        PsiClass testClass = null;

        if(testFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) testFile;
            testClass = javaFile.getClasses()[0];
        }

        RunManager runManager = RunManager.getInstance(project);

        RunnerAndConfigurationSettings runnerAndConfigurationSettings = runManager.createRunConfiguration(
                "sampleConfig", JUnitConfigurationType.getInstance().getConfigurationFactories()[0]);


        JUnitConfiguration tdaConfiguration = (JUnitConfiguration) runnerAndConfigurationSettings.getConfiguration();

        tdaConfiguration.getPersistentData().TEST_OBJECT = JUnitConfiguration.TEST_CLASS;
        tdaConfiguration.getPersistentData().setMainClass(testClass);
        tdaConfiguration.getPersistentData().setScope(TestSearchScope.WHOLE_PROJECT);
        tdaConfiguration.beClassConfiguration(testClass);

        tdaConfiguration.setModule(module);

        tdaConfiguration.setVMParameters("-ea");
        //tdaConfiguration.setWorkingDirectory("X:\\");

        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        ProgramRunnerUtil.executeConfiguration(project, runnerAndConfigurationSettings, executor);




        // The update method below is only called periodically so need
        //  to be careful to check for selected text
//        PsiFile data = e.getData(LangDataKeys.PSI_FILE);
//        final PsiFileFactory factory = PsiFileFactory.getInstance(project);
//        Language java = Language.findLanguageByID("JAVA");
//        final PsiFile file = factory.createFileFromText(java, "public static");
//        String byteCode = ByteCodeViewerManager.getByteCode(file);
//        System.out.println();
    }

    /**
     * Only make this action visible when text is selected.
     *
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
    }
}