using System;

namespace ClickCounterSingleton
{
    public sealed class ClickCounter
    {
        private static ClickCounter _instance;
        private static readonly object _lock = new object();

        public event Action CounterChanged;
        public int Count { get; private set; }

        private ClickCounter() { }

        public static ClickCounter Instance
        {
            get
            {
                lock (_lock)
                {
                    if (_instance == null)
                        _instance = new ClickCounter();
                    return _instance;
                }
            }
        }

        public void Increment()
        {
            Count++;
            CounterChanged?.Invoke();
        }
    }
}