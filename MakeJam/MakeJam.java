import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import jamsession.jam;

class MakeJam
{
  public static void main(String[] args)
    { System.out.println("--- Creating a jam session server.");
      boolean r = jam.startjamsession(60000);
      System.out.println("--- result = " + r);

      System.out.println("--- Creating another jam session server; should fail b/c one already running.");
      r = jam.startjamsession(60000);
      System.out.println("--- result = " + r);

      System.out.println("--- Stopping first jam session server");
      jam.stopjamsession();

      System.out.println("--- Creating a new server, this time with a custom name: fnord-server");
      r = jam.startjamsession(60000, "fnord-server");
      System.out.println("--- result = " + r);
      System.out.println("---When quit is requested, program will end jamsession thread and then stop.");
    }
}

