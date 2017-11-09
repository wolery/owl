//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : An idiomatic Scala interface to the core Java preferences API.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package util

import java.util.prefs.{Preferences ⇒ JavaPreferences}

/**
 * Represents a single named, mutable, persistent, value - a ''preference'' -
 * of type `α`.
 *
 * Preferences are persisted transparently in a platform specific manner. Each
 * has a sensible default value and can be reverted back to its default at any
 * time.
 *
 * The class supports a number of idiomatic styles for accessing the value:
 * {{{
 *    val p: Preference[Boolean] = ...
 *
 *    if (p.value)                                        // Get preference
 *      p.value =  ...                                    // Set preference
 *    if (p())                                            // Get preference
 *      p(false)   ...                                    // Set preference
 *    else
 *      p() =      ...                                    // Set preference
 * }}}
 *
 * @tparam α  The type of value being managed by the preference.
 *
 * @see    [[Preferences]]
 * @see    [[http://apps.tempel.org/PrefsEditor Mac OS X Prefs Editor]]
 * @see    [[https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html
 *         Core Java Preferences API]]
 * @author Jonathon Bell
 */
trait Preference[α]
{
  /**
   * The name of the preference.
   */
  def name: String

  /**
   * The current value of the preference.
   */
  def value: α

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  def value_=(value: α): Unit

  /**
   * Reverts the preference to its original default value.
   */
  def revert(): Unit

  /**
   * Returns the current value of the preference.
   */
  @inline final
  def apply(): α = value

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def apply (value: α): Unit = value_=(value)

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def update(value: α): Unit = value_=(value)
}

/**
 * Implements a factory for creating preferences of various primitive types.
 *
 * @author Jonathon Bell
 */
class Preferences private (private val m_imp: JavaPreferences)
{
  /**
   * Returns the named preference node in the same tree as this node, creating
   * it and any of its ancestors if they do not already exist.
   *
   * Accepts a relative or absolute path name. Relative path names (which do
   * not begin with the slash character `'/'` are interpreted relative to this
   * preference node.
   *
   * If the returned node did not exist prior to this call, this node and
   * any ancestors that were created by this call are not guaranteed
   * to become permanent until the `flush` method is called on
   * the returned node (or one of its ancestors or descendants).
   *
   * @param  path  The path name of the preference node to return.
   *
   * @return The specified preference node.
   * @see    `flush()`
   */
  def this(path: String) =
  {
    this(JavaPreferences.userRoot.node(path))
  }

  /**
   * Returns the preference node from the calling user's preference tree
   * that is associated (by convention) with the specified class's package.
   * The convention is as follows: the absolute path name of the node is the
   * fully qualified package name, preceded by a slash ({@code '/'}), and
   * with each period ({@code '.'}) replaced by a slash.  For example the
   * absolute path name of the node associated with the class
   * {@code com.acme.widget.Foo} is {@code /com/acme/widget}.
   *
   * <p>This convention does not apply to the unnamed package, whose
   * associated preference node is {@code <unnamed>}.  This node
   * is not intended for long term use, but for convenience in the early
   * development of programs that do not yet belong to a package, and
   * for "throwaway" programs.  <i>Valuable data should not be stored
   * at this node as it is shared by all programs that use it.</i>
   *
   * <p>A class {@code Foo} wishing to access preferences pertaining to its
   * package can obtain a preference node as follows: <pre>
   *    static Preferences prefs = Preferences.userNodeForPackage(Foo.class);
   * </pre>
   * This idiom obviates the need for using a string to describe the
   * preferences node and decreases the likelihood of a run-time failure.
   * (If the class name is misspelled, it will typically result in a
   * compile-time error.)
   *
   * <p>Invoking this method will result in the creation of the returned
   * node and its ancestors if they do not already exist.  If the returned
   * node did not exist prior to this call, this node and any ancestors that
   * were created by this call are not guaranteed to become permanent until
   * the {@code flush} method is called on the returned node (or one of its
   * ancestors or descendants).
   *
   * @param  c  The class for whose package a user preference node is desired.

   * @return The user preference node associated with the package of which `c`
   *         is a member.
   */
  def this(klass: Class[_]) =
  {
    this(JavaPreferences.userNodeForPackage(klass))
  }

  /**
   * Returns the named preference node in the same tree as this node,
   * creating it and any of its ancestors if they do not already exist.
   * Accepts a relative or absolute path name.  Relative path names
   * (which do not begin with the slash character {@code ('/')}) are
   * interpreted relative to this preference node.
   *
   * <p>If the returned node did not exist prior to this call, this node and
   * any ancestors that were created by this call are not guaranteed
   * to become permanent until the {@code flush} method is called on
   * the returned node (or one of its ancestors or descendants).
   *
   * @param pathName the path name of the preference node to return.
   * @return the specified preference node.
   * @see #flush()
   */
  def this(preferences: Preferences,path: String) =
  {
    this(preferences.m_imp.node(path))
  }

  /**
   * Returns the named preference node in the same tree as this node, creating
   * it and any of its ancestors if they do not already exist.
   *
   * Accepts a relative or absolute path name.  Relative path names (which  do
   * not begin with the slash character `'/'` are interpreted relative to this
   * preference node.
   *
   * If the returned node did not exist prior to this call,  this node and any
   * ancestors  that were created  by this call are  not guaranteed  to become
   * permanent until the `flush` method is called on the returned node (or one
   * of its ancestors or descendants).
   *
   * @param  path  The path name of the preference node to return.
   *
   * @return The specified preference node.
   * @see    `flush()`
   */
  def node(path: String): Preferences =
  {
    new Preferences(m_imp.node(path))
  }

 /**
   * Forces any modifications to the contents of this preference node and its
   * descendants to the persistent store. If this method returns successfully,
   * it is safe to assume that all changes made in the subtree rooted at this
   * node prior to the method invocation have become permanent.
   *
   * @see    `sync()`
   */
  def flush(): Unit = m_imp.flush()

  /**
   * Ensures that future reads from this preference  node and its  descendants
   * reflect any changes that were committed to the persistent store (from any
   * VM) prior to the `sync()` invocation. As a side-effect, forces changes in
   * the contents of this node and its descendants to the persistent store, as
   * if the `flush()` method had been invoked on this node.
   *
   * @see    `flush()`
   */
  def sync(): Unit = m_imp.sync()

  /**
   * Describes the type of a factory method that constructs a `Preference [α]`
   * from the name and default value of the preference.
   *
   * @tparam α  The type of value being managed by the generated preference.
   */
  type Atomic[α] = (Name,α) ⇒ Preference[α]

  /**
   * Describes the type of a factory method that constructs a `Preference [Seq[α]]`
   * from the name and default value of the preference.
   * @tparam α  The element type of the sequence being managed by the generated
   *             preference.
   */
  type Sequence[α] = (Name,Seq[α]) ⇒ Preference[Seq[α]]

  /**
   * Creates a `Preference[Int]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val int: Atomic[Int] = atomic(_.toInt)

  /**
   * Creates a `Preference[Bool]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val bool: Atomic[Bool] = atomic(_.toBoolean)

  /**
   * Creates a `Preference[Long]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val long: Atomic[Long] = atomic(_.toLong)

  /**
   * Creates a `Preference[Float]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val float: Atomic[Float] = atomic(_.toFloat)

  /**
   * Creates a `Preference[Double]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val double: Atomic[Double] = atomic(_.toDouble)

  /**
   * Creates a `Preference[String]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val string: Atomic[String] = atomic(_.toString)

  /**
   * Creates a `Preference[Seq[Int]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val ints: Sequence[Int] = sequence(_.toInt)

  /**
   * Creates a `Preference[Seq[Bool]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val bools: Sequence[Bool] = sequence(_.toBoolean)

  /**
   * Creates a `Preference[Seq[Long]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val longs: Sequence[Long] = sequence(_.toLong)

  /**
   * Creates a `Preference[Seq[Float]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val floats: Sequence[Float] = sequence(_.toFloat)

  /**
   * Creates a `Preference[Seq[Double]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val doubles: Sequence[Double] = sequence(_.toDouble)

  /**
   * Creates a `Preference[Seq[String]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val strings: Sequence[String] = sequence(_.toString)

  /**
   * TODO
   *
   * @tparam α        TODO
   * @param  parse    TODO
   * @param  print    TODO
   * @param  key      TODO
   * @param  default  TODO
   */
  private
  def atomic[α](parse: String ⇒ α,
                print: α ⇒ String = (a: α) ⇒ a.toString)
                (key: Name,default: α)
                : Preference[α] =
  {
    new Preference[α]
    {
      val name              = key
      val dflt              = print(default)
      def value             = parse(m_imp.get(key,dflt))
      def value_=(value: α) = m_imp.put(key,print(value))
      def revert()          = m_imp.remove(name)
      override
      def toString          = s"{$name = $value [$default]}"
    }
  }

  /**
   * TODO
   *
   * @tparam α      TODO
   * @param  parse  TODO
   */
  private
  def sequence[α](parse: String ⇒ α): Sequence[α] =
  {
    atomic(_.split("◇").map(parse),_.mkString("◇"))
  }
}

//****************************************************************************
