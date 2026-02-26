namespace ClickCounterSingleton
{
    partial class Form1
    {
        /// <summary>
        /// Обязательная переменная конструктора.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Освободить все используемые ресурсы.
        /// </summary>
        /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Код, автоматически созданный конструктором форм Windows

        /// <summary>
        /// Требуемый метод для поддержки конструктора — не изменяйте 
        /// содержимое этого метода с помощью редактора кода.
        /// </summary>
        private void InitializeComponent()
        {
            this.labelCount = new System.Windows.Forms.Label();
            this.buttonClick = new System.Windows.Forms.Button();
            this.buttonOpenForm = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // labelCount
            // 
            this.labelCount.AutoSize = true;
            this.labelCount.Font = new System.Drawing.Font("Microsoft Sans Serif", 20.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.labelCount.Location = new System.Drawing.Point(385, 118);
            this.labelCount.Name = "labelCount";
            this.labelCount.Size = new System.Drawing.Size(29, 31);
            this.labelCount.TabIndex = 0;
            this.labelCount.Text = "0";
            // 
            // buttonClick
            // 
            this.buttonClick.Location = new System.Drawing.Point(356, 213);
            this.buttonClick.Name = "buttonClick";
            this.buttonClick.Size = new System.Drawing.Size(75, 23);
            this.buttonClick.TabIndex = 1;
            this.buttonClick.Text = "Клик";
            this.buttonClick.UseVisualStyleBackColor = true;
            this.buttonClick.Click += new System.EventHandler(this.buttonClick_Click);
            // 
            // buttonOpenForm
            // 
            this.buttonOpenForm.Location = new System.Drawing.Point(331, 242);
            this.buttonOpenForm.Name = "buttonOpenForm";
            this.buttonOpenForm.Size = new System.Drawing.Size(130, 23);
            this.buttonOpenForm.TabIndex = 2;
            this.buttonOpenForm.Text = "Открыть второе окно";
            this.buttonOpenForm.UseVisualStyleBackColor = true;
            this.buttonOpenForm.Click += new System.EventHandler(this.buttonOpenForm_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.buttonOpenForm);
            this.Controls.Add(this.buttonClick);
            this.Controls.Add(this.labelCount);
            this.Name = "Form1";
            this.Text = "Form1";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label labelCount;
        private System.Windows.Forms.Button buttonClick;
        private System.Windows.Forms.Button buttonOpenForm;
    }
}

