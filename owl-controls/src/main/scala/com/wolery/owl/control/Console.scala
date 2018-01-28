//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A command line console control implemented as a TextArea.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//*  See Also: https://ss64.com/osx/syntax-bashkeyboard.html
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package control

import java.io.Writer
import java.lang.System.{lineSeparator ⇒ EOL}

import javafx.scene.control.TextArea
import javafx.scene.input.{KeyCode,KeyEvent}
import javafx.scene.input.KeyEvent.{KEY_PRESSED,KEY_TYPED}
import javafx.event.{ActionEvent,Event,EventHandler,EventType}

import scala.beans.BeanProperty
import scala.collection.mutable.Buffer

import util._

/**
 * A command line console control implemented as a JavaFX TextArea control.
 *
 * == Overview ==
 *
 * The Console control partitions the contents of its inherited text area into
 * three distinct areas:
 *
 *  - Output Area: A read-only area of text that extends from the beginning of
 *                 the underlying TextArea up to the prompt. It is intended to
 *                 record the output of whatever commands have previously been
 *                 accepted, and is modified by the client either by using the
 *                 appendText() API,  or by appending characters to the public
 *                 `writer` property.
 *
 *  - Prompt:      A read-only area of text that  extends from just beyond the
 *                 output area up to the input area. It is intended to display
 *                 some sort of short prompt string, and is assigned to by the
 *                 client either by appending characters to the output area as
 *                 above,  or else by assigning text to the `prompt` property.
 *
 *  - Input Area:  An editable area of text  that lies  immediately beyond the
 *                 prompt. The user interacts with the characters in this area
 *                 directly by entering keystrokes in the usual way, until the
 *                 input is finally accepted by typing the ENTER key, at which
 *                 point an ActionEvent is fired. The input area is exposed to
 *                 the client through the mutable `input` property.
 *
 * The client - usually a Controller of some sort -  registers interest in the
 * acceptance of an input line by assigning a handler to the `onAccept` bean
 * property.
 *
 * The client would typically respond to the event by processing the new input
 * string in some way, appending text to the output area, recording the string
 * in the command history, and eventually updating the prompt string, ready to
 * accept another line of input.
 *
 * An ActionEvent is also fired when the TAB key is pressed. This `Complete`
 * event gives the controller an opportunity  to prompt the user with possible
 * completions for the partially edited command waiting in the input area.
 *
 * == History ==
 *
 * The control maintains an array of strings known as the ''command history''.
 * Shell-style key bindings manipulate a cursor into this array, and enable an
 * element to be quickly summoned into the input area for further editing. The
 * history can also be incrementally searched for strings that match a certain
 * character pattern.
 *
 * The maximum number strings held in the command history is specified via the
 * `History Size` bean property.
 *
 * == Bean Properties ==
 *
 * The control exposes a number of bean properties that are accessible to the
 * Scene Builder application:
 *
 *  - `History Size`:  The maximum number of elements in the command history.
 *  - `onAccept`:      The event handler for the `Accept`   action event.
 *  - `onComplete`:    The event handler for the `Complete` action event.
 *
 * == Scala Properties ==
 *
 * The control also exposes a number of ordinary Scala properties:
 *
 *  - `output`:        The current contents of the output area (read only).
 *  - `prompt`:        The current contents of the prompt area.
 *  - `input`:         The current contents of the input area.
 *  - `writer`:        A java.io.Writer that appends text to the output area.
 *
 * == Internals ==
 *
 * The control maintains the following internal state variables:
 *
 *  - `m_prompt`:      The start of prompt area.
 *  - `m_input`:       The start of the input area.
 *  - `m_toggle`:      The position of the caret within the input area before
 *                     toggling back to the start of the input area.
 *  - `m_period`:      The position of the caret within the input area before
 *                     summoning the last word of the previous command back to
 *                     the input area.
 *  - `m_cursor`:      The index into the command history of the next command.
 *  - `m_latest:`      The index into the command history of the most recent
 *                     command to have been appended to the command history.
 *  - `m_command`:     A cyclic array of previously accepted command strings.
 *  - `m_filters`:     The event filters that define the current key bindings
 *                     and thus the overall behavior of the control.
 *
 * @see    [[https://ss64.com/osx/syntax-bashkeyboard.html Bash Keyboard Shortcuts]]
 *
 * @author Jonathon Bell
 */
class Console extends TextArea with util.Logging
{
  private type Filters = Seq[(EventType[KeyEvent],EventHandler[KeyEvent])]

  private var m_prompt : ℕ = 0                           // Start of prompt
  private var m_input  : ℕ = 0                           // Start of input
  private var m_toggle : ℕ = 0                           // Saved caret point
  private var m_period : Option[ℕ] = None                // Saved caret point
  private var m_cursor : ℕ = 0                           // The history cursor
  private var m_latest : ℕ = 0                           // The latest command
  private val m_command: Buffer[String] = Buffer.fill(500)("")
  private val m_filters: Filters = Seq((KEY_PRESSED,onKeyPressed _),
                                       (KEY_TYPED,  onKeyTyped   _))

  swap(Seq(),m_filters)                                  // Initialize filters

  /**
   * A bean property that records the current event handler for the `Accept`
   * action event.
   */
  @BeanProperty final
  var onAccept: EventHandler[ActionEvent] = _

  /**
   * A bean property that records the current event handler for the `Complete`
   * action event.
   */
  @BeanProperty final
  var onComplete: EventHandler[ActionEvent] = _

  /**
   * A bean property that records the maximum number of commands that can ever
   * reside in the command history at one time. Once this limit is exceeded, a
   * command is dropped from the history with each subsequent insertion.
   *
   * @return The maximum number of possible commands in the command history.
   */
  def getHistorySize: ℕ =
  {
    m_command.size                                       // Maximum legal size
  }

  /**
   * A bean property that records the maximum number of commands that can ever
   * reside in the command history at one time. Once this limit is exceeded, a
   * command is dropped from the history with each subsequent insertion.
   *
   * @param  size  The maximum number of possible commands recorded in the
   *               command history.
   */
  def setHistorySize(size: ℕ): Unit =
  {
    log.debug("setHistorySize({})",size)                 // Trace our location

    val min: ℕ = 1                                       // Minimum allowable
    val max: ℕ = 10                                      // Maximum allowable
    val δ  : ℤ = size - m_command.size                   // Delta from current

    if (size<min || max<size)                            // Size out of range?
    {
      log.warn(s"Bad history size $size: should be $min<=size<=$max")

      setHistorySize(clamp(min,size,max))                // ...clamp and retry
    }
    else
    if (δ > 0)                                           // Size is growing?
    {
      m_command ++= Seq.fill(δ)("")                      // ...append blanks
    }
    else
    if (δ < 0)                                           // Size is shrinking?
    {
      m_command.trimEnd(-δ)                              // ...drop the extras
    }

    assert(getHistorySize == size)                       // Check we succeeded
  }

  /**
   * The current contents of the output area.
   */
  def output: String =
  {
    getText(0,m_prompt)                                  // The output area
  }

  /**
   * The current contents of the prompt area.
   */
  def prompt: String =
  {
    getText(m_prompt,m_input)                            // The prompt area
  }

  /**
   * Updates the current contents of the prompt area with the given string.
   *
   * @param  string  The new prompt string.
   */
  def prompt_=(string: String): Unit =
  {
    super.replaceText(m_prompt,m_input,string)           // Replace the prompt

    setInputArea(m_prompt + string.length)               // Set up input area

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * The current contents of the input area.
   */
  def input: String =
  {
    getText(m_input,getLength)                           // The input area
  }

  /**
   * Updates the current contents of the input area with the given string.
   *
   * @param  string  The new input string.
   */
  def input_=(string: String): Unit =
  {
    super.replaceText(m_input,getLength,string)          // Replace the input

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * A Writer that appends text to the output area.
   *
   * Characters written to this object are appended to the output area and the
   * prompt and input areas are then  re-established to immediately follow the
   * newly added text.
   */
  val writer: Writer = new Writer
  {
    def close: Unit = {}                                 // Nothing to do
    def flush: Unit = {}                                 // Nothing to do
    def write(cbuf: Array[Char],off: ℕ,len: ℕ): Unit =
    {
      appendText(new String(cbuf.slice(off,off + len)))  // Add to output area
    }
  }

  /**
   * Replaces characters in the range [`start`, `end`) with the given string,
   * provided that this region lies within the input area.
   *
   * @param  start  The index of the first character within the input area to
   *                be replaced.
   * @param  end    The index of the final character within the input area to
   *                to be replaced.
   * @param  text   The text with which replace the given range of characters
   *                within the input area.
   */
  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
    assert(isIncreasing(0,start,end,getLength))          // Validate arguments

    log.trace("replaceText({})",s"$start,$end,$text")    // Trace our location

    if (start >= m_input)                                // In the input area?
    {
      super.replaceText(start,end,text)                  // ...fine, go ahead
    }

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * Appends a sequence of characters to the underlying output area and resets
   * the prompt and input areas to start immediately beyond the appended text.
   *
   * @param  text  The text to append to the output area.
   */
  override
  def appendText(text: String) =
  {
    log.trace("appendText({})",text)                     // Trace out location

    super.appendText(text)                               // Append characters

    m_prompt = getLength                                 // Reset prompt area

    setInputArea(m_prompt)                               // Reset input area

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * Appends a sequence of characters to the underlying output area and resets
   * the prompt and input areas to start immediately beyond the appended text.
   * In addition, terminates the appended text with the platform specific end-
   * of-line string.
   *
   * @param  text  The text to append to the output area.
   */
  def appendLine(text: String) =
  {
    log.trace("appendLine({})",text)                     // Trace our location

    appendText(text + EOL)                               // Append an EOL char

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * Selects the characters in the range [`anchor`, `caret`), clamping both of
   * the arguments if necessary to ensure  that the region is contained within
   * the input area.
   *
   * @param  anchor  The new anchor position.
   * @param  caret   The new caret position.
   */
  override
  def selectRange(anchor: ℕ,caret: ℕ): Unit =
  {
    log.trace("selectRange({},{})",anchor,caret)         // Trace our location

    val n = getLength                                    // End of input area
    val a = clamp(m_input,anchor,n)                      // Clamp the anchor
    val c = clamp(m_input,caret, n)                      // Clamp the caret

    super.selectRange(a,c)                               // Now select region

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * Accepts the current contents of the input area, firing an action event to
   * notify any client that may have registered interest in this event.
   */
  def accept(): Unit =
  {
    log.info("accept({})",input)                         // Trace our location

    super.appendText(EOL)                                // Append an EOL char

    if (onAccept != null)                                // We have a handler?
    {
      onAccept.handle(new ActionEvent)                   // ...fire the event
    }

    setInputArea(getLength)                              // Advance input area
  }

  /**
   * Fires an action event to alert a client that may have registered interest
   * in this event.
   *
   * Intended to enable a client to prompt the user with possible completions
   * for the current partially edited command.
   */
  def complete(): Unit =
  {
    log.info("complete({})",input)                       // Trace our location

    if (onComplete != null)                              // We have a handler?
    {
      onComplete.handle(new ActionEvent)                 // ...fire the event
    }
  }

  /**
   * Repositions the caret at the start of the input area.
   */
  override
  def home(): Unit =
  {
    log.debug("home()")                                  // Trace our location

    m_toggle = m_input                                   // Also update toggle

    positionCaret(m_input)                               // Reposition caret
  }

  /**
   * Moves the caret forward by one character.
   */
  override
  def forward(): Unit =
  {
    log.debug("forward()")                               // Trace our location

    when (getCaretPosition < getLength)                  // Room to move into?
    {
      super.forward()                                    // ...ok, so move it
    }

    positionCaret(getCaretPosition)                      // Clear selection
  }

  /**
   * Moves the caret backward by one character.
   */
  override
  def backward(): Unit =
  {
    log.debug("backward()")                              // Trace our location

    when (m_input < getCaretPosition)                    // Room to move into?
    {
      super.backward()                                   // ...ok, so move it
    }

    positionCaret(getCaretPosition)                      // Clear selection
  }

  /**
   * Toggle the caret between its current location and the start of the input
   * area.
   */
  def toggleHome(): Unit =
  {
    log.debug("toggleHome({})",m_toggle)                 // Trace our location

    val c = getCaretPosition                             // The caret position

    if (m_input < c)                                     // Not at start yet?
    {
      m_toggle = c                                       // ...record position

      positionCaret(m_input)                             // ...jump back home
    }
    else                                                 // Already at home
    {
      positionCaret(m_toggle)                            // ...jump back then
    }
  }

  /**
   * Deletes the character immediately preceding the caret.
   */
  override
  def deletePreviousChar(): Bool =
  {
    log.debug("deletePreviousChar()")                    // Trace our location

    when (m_input < getCaretPosition)                    // Room to move into?
    {
      super.deletePreviousChar()                         // ...ok, delete char
    }
  }

  /**
   * Deletes the character immediately following the caret.
   */
  override
  def deleteNextChar(): Bool =
  {
    log.debug("deleteNextChar()")                        // Trace our location

    when (getCaretPosition + 1 < getLength)              // Room to move into?
    {
      super.deletePreviousChar()                         // ...ok, delete char
    }
  }

  /**
   * Deletes the word immediately preceding the caret.
   */
  def deletePreviousWord(): Unit =
  {
    log.debug("deletePreviousWord()")                    // Trace our location

    val c = getCaretPosition                             // The caret position
    previousWord()                                       // Jump over the word
    deleteText(getCaretPosition,c)                       // Delete characters
  }

  /**
   * Deletes the word immediately following the caret.
   */
  def deleteNextWord(): Unit =
  {
    log.debug("deleteNextWord()")                        // Trace our location

    val c = getCaretPosition                             // The caret position
    endOfNextWord()                                      // Jump over the word
    deleteText(c,getCaretPosition)                       // Delete characters
  }

  /**
   * Cuts the word immediately preceding the caret onto the clipboard.
   */
  def cutPreviousWord(): Unit =
  {
    log.debug("cutPreviousWord()")                       // Trace our location

    when (m_input > getCaretPosition)                    // Room to move into?
    {
      selectPreviousWord()                               // ...select the word
      cut()                                              // ...and then cut it
    }
  }

  /**
   * Cuts the characters between the caret and the end of the input area onto
   * the clipboard.
   */
  def cutToEnd(): Unit =
  {
    log.debug("cutToEnd()")                              // Trace our location

    selectRange(getCaretPosition,getLength)              // Select characters
    cut()                                                // Cut onto clipboard
  }

  /**
   * Cuts the characters between the caret and the beginning of the input area
   * onto the clipboard.
   */
  def cutToHome(): Unit =
  {
    log.debug("cutToHome()")                             // Trace our location

    selectRange(m_input,getCaretPosition)                // Select characters
    cut()                                                // Cut onto clipboard
  }

  /**
   * Swaps the characters either side of the caret, or, if the caret is now at
   * the end of the input area, the two characters immediately preceding it.
   */
  def swapChars(): Unit =
  {
    log.debug("swapChars()")                             // Trace our location

    var c = getCaretPosition                             // The caret position
    val e = getLength                                    // The very last char

    when (m_input + 2 <= c)                              // Something to swap?
    {
      if (c < e)                                         // ...char on right?
      {
        c -= 1                                           // ....back up a char
      }
      else                                               // No, 2 on the left
      {
        c -= 2                                           // ...back up 2 chars
      }

      val s = getText(c,c+2)                             // Grab the two chars
      val t = s"${s(1)}${s(0)}"                          // Swap them around

      replaceText(c,c+2,t)                               // Replace characters
    }
  }

  /**
   * Applies the given function to the first character of the word following
   * the caret.
   *
   * @param  change  The function to apply to the character.
   */
  def changeChar(change: String ⇒ String): Unit =
  {
    log.debug("changeChar()")                            // Trace our location

    var c = getCaretPosition                             // The caret position

    when (c < getLength)                                 // Room to move into?
    {
      endOfNextWord()                                    // ...jump forward
      previousWord()                                     // ...then back again
      c = getCaretPosition                               // ...so at next word
      replaceText(c,c+1,change(getText(c,c+1)))          // ...transform char
      endOfNextWord()                                    // ...advance caret
    }
  }

  /**
   * Applies the given function to each character of the word following the
   * caret.
   *
   * @param  change  The function to apply to each character of the word.
   */
  def changeWord(change: String ⇒ String): Unit =
  {
    log.debug("changeWord()")                            // Trace our location

    var c = getCaretPosition                             // The caret position

    when (c < getLength)                                 // Room to move into?
    {
      endOfNextWord()                                    // ...jump forward
      val e = getCaretPosition                           // ...found the end
      replaceText(c,e,change(getText(c,e)))              // ...transform chars
    }
  }

  /**
   * Appends the given string to the command history, possibly overwriting the
   * oldest string in the history if the array is now full.
   *
   * @param  command  The command string to append to the command history.
   */
  def addCommand(command: String): Unit =
  {
    log.debug("addCommand({})",command)                  // Trace our location

    m_command(m_latest % m_command.size) = command       // Update the latest
    m_latest += 1                                        // Advance the index
    m_cursor  = m_latest                                 // Advance the cursor
  }

  /**
   * Returns the given element of the command history.
   */
  def getCommand(index: ℕ): String =
  {
    log.debug("getCommand({})",index)                    // Trace our location

    m_command(index % m_command.size)                    // Use modular index
  }

  /**
   * Adjusts the position of the history cursor by the given delta.
   *
   * @param  δ  A number of slots to displace the cursor by, positive to move
   *            forward, negative to move back.
   */
  def cursor(δ: ℤ): Unit =
  {
    log.debug("cursor({})",δ)                            // Trace our location

    when (commandRange.inclusive.contains(m_cursor + δ)) // Is still in range?
    {
      m_cursor += δ                                      // ...adjust cursor
    }

    input = if (commandRange.contains(m_cursor)) getCommand(m_cursor) else ""
  }

  /**
   * Formats the current contents of the command history and appends it to the
   * given writer.
   *
   * @param  count   The number of commands to include in the listing.
   * @param  writer  The writer to which we append the formatted output.
   */
  def listCommands(count: ℕ = m_command.size,writer: Writer = this.writer): Unit =
  {
    log.debug("listCommands()")                          // Trace our location

    for (i ← commandRange.takeRight(count))              // For each command
    {
      writer.append(f"${i+1}%5d  ${getCommand(i)}%s\n")  // ...format command
    }
  }

  /**
   * Summon the last word of the given command into the input area just before
   * the caret.
   *
   * The given is referenced by its offset in the command history relative to
   * the command cursor.
   *
   * @param  δ  A number of slots to displace the cursor by, positive to move
   *            forward, negative to move back.
   */
  def lastWord(δ: ℤ): Unit =
  {
    log.debug("lastArgument({})",m_cursor)               // Trace our location

    when (commandRange.inclusive.contains(m_cursor + δ)) // Is still in range?
    {
      m_cursor += δ                                      // ...adjust cursor
    }

    val c = getCaretPosition                             // The caret position

    if (m_period.isEmpty)                                // Not yet been set?
    {
      m_period = Some(c)                                 // ...save position
    }

    replaceText(m_period.get,c,m_command(m_cursor).split(" ").last)
  }

  /**
   * Search the command history incrementally in reverse for a certain pattern
   * of characters.
   *
   * Places the console into a 'Search' mode in which the keyboard is remapped
   * so that keystrokes interactively update the search pattern.
   */
  def findCommand(): Unit =
  {
    log.debug("findCommand({})",input)                   // Trace our location

    new Search(prompt,input,m_filters)                   // Enter search mode
  }

  /**
   * Implements an incremental reverse search of the command history.
   *
   * Constructed from a snapshot of the enclosing parent's mutable state, this
   * object temporarily re-maps the keyboard by swapping the KeyEvent filters.
   * This places the Console into a mode in which characters typed by the user
   * refine a  search pattern that filters the command history and updates the
   * input area interactively.
   *
   * Canceling the search restores the event filters, prompt, and input areas,
   * and may also fire an Accept event.
   *
   * @param  old_prompt   The current contents of the prompt area.
   * @param  old_input    The current contents of the input area.
   * @param  old_filters  The current KeyEvent filters.
   */
  private
  class Search (old_prompt: String,old_input: String,old_filters: Filters)
  {
    var m_cursor : ℕ           = 0                       // Current position
    var m_pattern: String      = old_input               // Current pattern
    var m_matches: Seq[String] = Seq()                   // Current matches
    val m_filters: Filters     = Seq((KEY_PRESSED,onKeyPressed _ ),
                                     (KEY_TYPED,  onKeyTyped   _ ))

    swap(old_filters,m_filters)                          // Enter search mode

    refine()                                             // Refine the search

    /**
     * Cancels the search by restoring the KeyEvent filters, prompt, and input
     * areas, and optionally placing the given string back on the command line
     * for subsequent editing.
     *
     * @param  string  The new contents of the input area.
     */
    def cancel(string: String = old_prompt): Unit =
    {
      log.debug("search.cancel({})",string)              // Trace our location

      prompt = old_prompt                                // Restore the prompt
      input  = string                                    // Restore the input
      swap(m_filters,old_filters)                        // Cancel search mode

      assert(isConsistent)                               // Check consistency
    }

    /**
     * Updates the prompt area with the current search pattern, and input area
     * with the given string.
     *
     * @param  string  The new contents of the input area.
     */
    def update(string: String = "") : Unit =
    {
      log.debug("search.update({})",string)              // Trace our location

      prompt = s"(reverse-i-search)'$m_pattern': "       // Update prompt area
      input  = string                                    // Update input area
    }

    /**
     * Updates the prompt area with the current search pattern, and input area
     * with the given string.
     *
     * @param  string  The new contents of the input area.
     */
    def backspace(): Unit =
    {
      log.debug("search.backspace()")                    // Trace our location

      m_pattern = m_pattern.dropRight(1)                 // Drop one character

      refine()                                           // Refine the search
    }

    /**
     * Adjust the command history cursor by the given delta.
     *
     * @param  δ  The number of slots to displace the cursor by, positive to
     *            move forward, negative to move back.
     */
    def cursor(δ: ℤ): Unit =
    {
      log.debug("search.moveCursor({})",δ)               // Trace our location

      when (m_matches.isDefinedAt(m_cursor + δ))         // Is still in range?
      {
        m_cursor += δ                                    // ...displace cursor
        val m = m_matches(m_cursor)                      // ...get the command
        update(m)                                        // ...set the input
        positionCaret(m_input+m.indexOfSlice(m_pattern)) // ...point at match
      }
    }

    /**
     * Refines the current search to incorporate the given characters into the
     * search pattern.
     *
     * @param  chars  The characters to append to the current search pattern.
     */
    def refine(chars: String = ""): Unit =
    {
      log.debug("search.refine({})",chars)               // Trace our location

      m_pattern+= chars                                  // Update the pattern

      m_matches = m_command.filter(_.contains(m_pattern))// Filter the history

      if (m_matches.nonEmpty)                            // Matching commands?
      {
        m_cursor = m_matches.size - 1                    // ...the final match

        cursor(0)                                        // ...position cursor
      }
      else                                               // No commands found
      {
        update()                                         // ...clear the input
      }
    }

    /**
     * Processes the current keystroke.
     *
     * @param  e  The current keystroke.
     */
    def onKeyPressed(e: KeyEvent): Unit =
    {
      log.debug("onKeyPressed{} [{}]",getKeyCombination(e),e.getCharacter,"")

      import KeyCode._                                   // For key code names

      getKeyCombination(e) match                         // Which combination?
      {
        case ('_,BACK_SPACE) ⇒ backspace()               // ...back up a char
        case ('^,G)          ⇒ cancel()                  // ...cancel search
        case ('^,R)          ⇒ cursor(-1)                // ...previous match
        case ('^,S)          ⇒ cursor(+1)                // ...next match
        case ('_,ENTER)      ⇒ cancel(input);accept()    // ...accept match
        case (_,TAB |ESCAPE
               |UP  |DOWN
               |LEFT|RIGHT)  ⇒ cancel(input)             // ...cancel search
        case  _              ⇒                           // ...discard event
      }

      e.consume()                                        // We have handled it
    }

    /**
     * Processes the current keystroke.
     *
     * @param  e  The current keystroke.
     */
    def onKeyTyped(e: KeyEvent): Unit =
    {
      log.debug("onKeyTyped  {} [{}]",getKeyCombination(e),e.getCharacter,"")

      val c = e.getCharacter                             // Character entered

      if (c.nonEmpty && isSearchable(c(0)))              // Can search on it?
      {
        refine(c)                                        // ...add to pattern
      }

      e.consume()                                        // We have handled it
    }
  } /************************************************************************/

  /**
   * Processes the current keystroke.
   *
   * @param  e  The current keystroke.
   */
  private
  def onKeyTyped(e: KeyEvent): Unit =
  {
    log.debug("onKeyTyped  {} [{}]",getKeyCombination(e),e.getCharacter,"")

 /* On OS X, the system handles Alt key combinations specially, and at a low
    level, to insert certain alternate glyphs and diacritical marks into the
    underlying text area...*/

    if (e.isAltDown)                                     // Is ALT key down?
    {
      e.consume()                                        // ...discard event
    }
  }

  /**
   * Processes the current keystroke.
   *
   * @param  e  The current keystroke.
   */
  private
  def onKeyPressed(e: KeyEvent): Unit =
  {
    log.debug("onKeyPressed{} [{}]",getKeyCombination(e),e.getCharacter,"")

    import KeyCode._                                     // For key code names

    var period   = true                                  // Assume we reset .
    var consumed = true                                  // Assume we consume

    getKeyCombination(e) match                           // Which combination?
    {
   // Cursor Movement:

      case ('^,A|LEFT)      ⇒ home()
      case ('^,E|RIGHT)     ⇒ end()
      case ('^,F)           ⇒ forward()
      case ('^,B)           ⇒ backward()
      case ('⌥,F)           ⇒ endOfNextWord()
      case ('⌥,B)           ⇒ previousWord()
      case ('^,X)           ⇒ toggleHome()

   // Character Selection

      case ('⇧^,A|LEFT)     ⇒ selectHome()
      case ('⇧^,E|RIGHT)    ⇒ selectEnd()

   // Input Editing:

      case ('⌥,BACK_SPACE)  ⇒ deletePreviousWord()
      case ('⌥,D)           ⇒ deleteNextWord()
      case ('^,D)           ⇒ deleteNextChar()
      case ('^,H)           ⇒ deletePreviousChar()
      case ('^,W)           ⇒ cutPreviousWord()
      case ('^,K)           ⇒ cutToEnd()
      case ('^,U)           ⇒ cutToHome()
      case ('^,T)           ⇒ swapChars()
      case ('^,Y)           ⇒ paste()
      case ('⌥,U)           ⇒ changeWord(_.toUpperCase)
      case ('⌥,L)           ⇒ changeWord(_.toLowerCase)
      case ('⌥,C)           ⇒ changeChar(_.toUpperCase)

   // Command History:

      case ('⌥,R)           ⇒ cursor(0)
      case ('^,P)|('_,UP)   ⇒ cursor(-1)
      case ('^,N)|('_,DOWN) ⇒ cursor(+1)
      case ('^,R)           ⇒ findCommand()
      case ('⌥,PERIOD)      ⇒ lastWord(-1);period = false// No, still need it
      case ('⌥,SLASH)       ⇒ lastWord(+1);period = false// No, still need it

   // Events:

      case ('_,ENTER)       ⇒ accept()
      case ('_,TAB)         ⇒ complete()

   // Explicitly Disabled:

      case ('◆ | '⇧◆,Z)     ⇒

   // Anything Else...

      case  _               ⇒ consumed = false
    }

    if (period)                                          // Reset the period?
    {
      m_period = None                                    // ...ok, reset it
    }

    if (consumed)                                        // Did we consume it?
    {
      e.consume()                                        // ...mark consumed
    }
  }

 /**
   * Returns true if the given character is one we consider 'searchable'. This
   * currently includes the ASCII printing characters,  but could be broadened
   * to include other Unicode symbols in the future.
   *
   * @param  character  A character to consider for inclusion in the current
   *                    search pattern.
   *
   * @return `true` if the given character is on that can be searched for.
   */
  @inline private
  def isSearchable(character: Char): Bool =
  {
    character.isBetween(0x20,0x7E)                       // Can it be printed?
  }

  /**
   * Defers the given side-effecting computation to run in the near future on
   * a background thread, after the calling function has returned.
   *
   * @param  action  A side-effecting action to perform in the near future,
   */
  @inline private
  def defer[α](action: ⇒ α): Unit =
  {
    javafx.application.Platform.runLater(() ⇒ action)    // Convert to lambda
  }

  /**
   * Re-establish the input area at the given offset into our underlying text
   * area.
   *
   * @param  input  The offset into the underlying text area at which to start
   *                the new input area.
   */
  private
  def setInputArea(input: ℕ): Unit =
  {
    log.trace("setInputArea({},{})",m_prompt,input)      // Trace our location

    m_input  = input                                     // Save  input offset
    m_toggle = input                                     // Clear caret toggle
    m_period = None                                      // Clear insert point
    m_cursor = m_latest

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * Returns the range of indices into the command history array that now hold
   * valid command strings.
   *
   * This function, together with the use of modular arithmetic when indexing
   * into the array, implement the cyclic behavior of the command history.
   *
   * @return The range of indices into the command history that now hold valid
   *          command strings.
   */
  private
  def commandRange: Range =
  {
    if (getCommand(m_latest).isEmpty)                    // Not yet written to?
      0 until m_latest                                   // ...initial segment
    else                                                 // Something in there
      m_latest - m_command.size until m_latest           // ...then cycle back
  }

  /**
   * Returns an encoding of the key code and its possible modifiers in a form
   * that as easy and efficient to pattern match on.
   *
   * Encodes the set of modifier keys in effect when the key was pressed as a
   * as one of 8 symbols consisting of the characters:
   *
   *  - `⇧`  The Shift key is depressed.
   *  - `^`  The Control key is depressed.
   *  - `⌥`  The Alt/Option key is depressed.
   *  - `◆`  The Command/Meta key is depressed.
   *
   * For example, we encode key combination '`Shift + Option X`' as `('⇧⌥, X)`.
   *
   * @param  e  The key event to examine and encode.
   *
   * @return A (Symbol,KeyCode) pair that efficiently encodes the combination
   *         of keys that interests us.
   */
  private
  def getKeyCombination(e: KeyEvent): (Symbol,KeyCode) =
  {
    var                   s  = ""                        // The result symbol
    if (e.isShiftDown)    s += '⇧'                       // Is Shift pressed?
    if (e.isControlDown)  s += '^'                       // Is Cntrl pressed?
    if (e.isAltDown)      s += '⌥'                       // Is Alt   pressed?
    if (e.isMetaDown)     s += '◆'                       // Is Meta  pressed?

    if (s.isEmpty())                                     // No modifier keys?
      ('_,       e.getCode)                              // ...encode as '_
    else                                                 // Yes, got something
      (Symbol(s),e.getCode)                              // ...intern and pair
  }

  /**
   * Performs the given action for its side effect if the given condition is
   * `true`, or 'beep's if not, and returns the value of the given condition.
   *
   * @param  condition  The boolean condition to test.
   * @param  action     An action to perform if the `condition` is `true`.
   *
   * @return The value of the given condition.
   */
  private
  def when(condition: Bool)(action: ⇒ Unit): Bool =
  {
    if (condition)                                       // Is condition true?
    {
      action                                             // ...well go on then
    }
    else                                                 // Sorry, no can do
    {
      beep()                                             // ...ring the bell
    }

    condition                                            // Allow for chaining
  }

  /**
   * Swaps the KeyEvent filters that define the control's active key bindings.
   *
   * Effects a switch between the control's regular and 'search' modes.
   *
   * @param  was  The current KeyEvent filters handling our key events.
   * @param  now  The new KeyEvent filters that will handle future key events.
   */
  private
  def swap(was: Filters,now: Filters): Unit = defer
  {
    was.foreach{case (t,f) ⇒ removeEventFilter(t,f)}     // Remove old filters
    now.foreach{case (t,f) ⇒ addEventFilter   (t,f)}     // Append new filters
  }

  /**
   * Returns true if the object appears to be in a consistent state.
   *
   * Centralizes a number of consistency checks that otherwise tend to clutter
   * up the code.  Since only ever called from within assertions, these can be
   * eliminated from the release build by the compiler entirely.
   *
   * @return true - always.
   */
  private
  def isConsistent: Bool =
  {
    assert(isIncreasing(0,m_prompt,m_input,m_toggle,getLength))
    true
  }
}

//****************************************************************************
