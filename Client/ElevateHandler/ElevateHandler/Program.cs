using System;
using System.Diagnostics;
using System.ComponentModel;

namespace TestElevate
{
    class Program
    {
        static void Main(string[] args)
        {
            ArgumentsHandler argHandler = new ArgumentsHandler(args);

            if (argHandler.ParseArguments())
            {
                String app = argHandler.ApplicationToRun;
                String app_args = argHandler.Arguments;
                Boolean elevate = argHandler.Elevate;

                ProcessStartInfo psInfo = new ProcessStartInfo();
                psInfo.Arguments = app_args;
                psInfo.FileName = app;
                psInfo.UseShellExecute = true;

                if (elevate && true == IsVistaOrBetter())
                {
                    psInfo.Verb = "runas";
                }

                try
                {
                    Process p = Process.Start(psInfo);                   
                    p.WaitForExit();                               
                }               
                catch (Win32Exception ex)
                {                    
                    Console.WriteLine(ex.Message);
                    Environment.Exit(5);
                }
            }
        }

        static Boolean IsVistaOrBetter()
        {
            if ((Environment.OSVersion.Version.Major >= 6) &&
                 (Environment.OSVersion.Version.Minor >= 0))
            {
                return (true);
            }

            return (false);
        }
    }
}