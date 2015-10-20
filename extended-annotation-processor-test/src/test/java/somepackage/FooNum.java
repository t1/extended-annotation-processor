package somepackage;

public enum FooNum {
    X,
    Y,
    Z {
        @Override
        public String toString() {
            return "Z!";
        }
    };
}
