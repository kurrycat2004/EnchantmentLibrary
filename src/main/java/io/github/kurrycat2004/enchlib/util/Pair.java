package io.github.kurrycat2004.enchlib.util;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Pair<A, B>(A first, B second) {}
