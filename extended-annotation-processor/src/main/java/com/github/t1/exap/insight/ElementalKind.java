package com.github.t1.exap.insight;

/// The kind of an elemental, sorted from most specific to more general,
/// so sorting will result in the most specific element first,
/// e.g. a method before the class it's in.
///
/// As we don't yet support all possible kinds of subclasses to Elemental, this is enum is also only a subset.
public enum ElementalKind {
    ANNOTATION, PARAMETER, FIELD, METHOD, TYPE, PACKAGE
}
