package System_Utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

import static java.lang.System.setErr;
import static java.util.prefs.Preferences.systemRoot;

/**
 * From https://stackoverflow.com/users/607407/tom%C3%A1%C5%A1-zato 's answer
 * https://stackoverflow.com/questions/4350356/detect-if-java-application-was-run-as-a-windows-admin
 * https://www.coder.work/article/75451
 * 检查是否是管理员
 */
class AdministratorChecker
{
    public static final boolean IS_RUNNING_AS_ADMINISTRATOR;

    // 类初始化运行的
    static
    {
        IS_RUNNING_AS_ADMINISTRATOR = isRunningAsAdministrator();
    }

    private static boolean isRunningAsAdministrator()
    {
        Preferences preferences = systemRoot();

        synchronized (System.err)
        {
            setErr(new PrintStream(new OutputStream()
            {
                @Override
                public void write(int b)
                {
                }
            }));

            try
            {
                preferences.put("foo", "bar"); // SecurityException on Windows
                preferences.remove("foo");
                preferences.flush(); // BackingStoreException on Linux
                return true;
            } catch (Exception exception)
            {
                return false;
            } finally
            {
                setErr(System.err);
            }
        }
    }
}