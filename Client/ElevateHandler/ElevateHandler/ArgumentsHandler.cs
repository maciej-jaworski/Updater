using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TestElevate
{
    internal class ArgumentsHandler
    {

        private string[] rawArgs;
        private String applicationToRun;
        private Boolean elevate;
        private StringBuilder argBuilder;

        public ArgumentsHandler(string[] args)
        {
            rawArgs = args;
            argBuilder = new StringBuilder();
        }

        public Boolean Elevate
        {
            get { return elevate; }
            set { elevate = value; }
        }

        public String ApplicationToRun
        {
            get { return applicationToRun; }
            set { applicationToRun = value; }
        }

        public String Arguments
        {
            get { return (argBuilder.ToString().Trim()); }
        }

        public Boolean ParseArguments()
        {
            if (0 == rawArgs.Length)
                return (false);

            elevate = Boolean.Parse(rawArgs[0]);

            ApplicationToRun = rawArgs[1];

            int currentIndex = 2;

            while (currentIndex < rawArgs.Length)
            {
                String currArg = rawArgs[currentIndex];
                String fmt = "{0} ";
                if (true == currArg.Contains(" "))
                {
                    fmt = "\"{0}\" ";
                }
                argBuilder.AppendFormat(fmt, currArg);
                ++currentIndex;
            }

            if (true == String.IsNullOrEmpty(ApplicationToRun))
                return (false);

            return (true);
        }
    }
}