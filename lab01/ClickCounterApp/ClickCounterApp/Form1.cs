using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClickCounterApp
{
    public partial class Form1 : Form
    {
        private ClickCounter _counter;
        public Form1(ClickCounter counter)
        {
            InitializeComponent();
            _counter = counter;
        }

        private void buttonClick_Click(object sender, EventArgs e)
        {
            _counter.Count++;
            UpdateLabel();
        }
        public void UpdateLabel()
        {
            labelCount.Text = _counter.Count.ToString();
        }

        private void buttonOpenForm_Click(object sender, EventArgs e)
        {
            Form2 form2 = new Form2(_counter);
            form2.Show();
        }
    }
}