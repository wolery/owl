//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*  Arrows  : ⇒ → ←
//*  Ops     : ∷ ⧺ ≡ ≠ ≤ ≥ ≯ ≮ ∧ ∨ × ÷ ∘ ∀ ∃ ∈ ∉ ∪ ∩ ⊥ ∪ ▷ ▶ ◀ ◁ • ⋅ ε ⊢⊣
//*  Music   : 𝄫♭♮♯𝄪
//*  Super   : ⁰¹²³⁴⁵⁶⁷⁸⁹⁻⁺
//*  Sub     : ₀₁₂₃₄₅₆₇₈₉₋₊
//*  Math    : 𝔹 ℝ ℚ ℤ
//*  Greek   : αβγδεζηθιικλμνξοπρστυφχψω
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl;

//****************************************************************************
/**
 * Describes the carrier set for the regular action of an additive group.
 *
 * Technically, a ''G''-torsor is a set ''S'' whose elements are 'acted upon'
 * by the elements of a group ''G'', in the sense that there is an isomorphism
 * from ''G'' into ''Sym(S)'' (the symmetry group of ''S'') and  that furthermore,
 * that this action is sharply transitive.
 *
 * Instances must satisfy the following axioms:
 * {{{
 * A1      s + (g₁ + g₂)  =  (s + g₁) + g₂
 * A2              s + 0  =  s
 * A3              s - g  =  s + (-g)
 * A4     s₁ + (s₁ ⊣ s₂)  =  s₂
 * A5     s₂ + (s₁ ⊢ s₂)  =  s₁
 * }}}
 *
 *
 * In other words:
 *
 *  - the (curried form of the) binary operator + is a homomorphism from G to
 *    Sym(S), the symmetry group of S
 *
 *  - the image of the group identity under this homomorphism is the identity
 *    permutation of S
 *
 *  - the notation s - g is just shorthand for the action of group inverses
 *
 * @tparam G an additive group that acts regularly upon the torsor ''S''
 * @tparam S a carrier set for the action of G that extends this trait
 * @see    http://en.wikipedia.org/wiki/Principal_homogeneous_space
 */
