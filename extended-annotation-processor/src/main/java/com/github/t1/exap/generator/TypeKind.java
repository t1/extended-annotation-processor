package com.github.t1.exap.generator;

public enum TypeKind {
    CLASS {
        @Override
        public String toString() {
            return "class";
        };
    },
    INTERFACE {
        @Override
        public String toString() {
            return "interface";
        };
    },
    ANNOTATION {
        @Override
        public String toString() {
            return "@interface";
        };
    };
}
