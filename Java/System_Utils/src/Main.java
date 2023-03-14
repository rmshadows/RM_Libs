import System_Utils.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        LinkedList<String> r = System_Utils.execCommandByRuntime("cat as", false, true);
    }


}