using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClickCounterSingleton
{
    public partial class Form2 : Form
    {
        public Form2()
        {
            InitializeComponent();
        }

        public void UpdateLabel()
        {
            labelCount.Text = ClickCounter.Instance.Count.ToString();
        }
   
        private void buttonClick_Click(object sender, EventArgs e)
        {
            ClickCounter.Instance.Increment();
            UpdateLabel();
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            UpdateLabel();
            ClickCounter.Instance.CounterChanged += UpdateLabel;
        }
    }
}
