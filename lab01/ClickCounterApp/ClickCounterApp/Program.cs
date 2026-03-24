using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClickCounterApp
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            // Создаём один объект счётчика
            ClickCounter counter = new ClickCounter();

            // Передаём его в Form1
            Application.Run(new Form1(counter));
        }
    }
}