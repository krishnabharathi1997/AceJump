package org.acejump.control

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import org.acejump.control.Handler.regexSearch
import org.acejump.label.Pattern.ALL_WORDS
import org.acejump.label.Pattern.LINE_MARK
import org.acejump.search.getNameOfFileInEditor
import org.acejump.view.Boundary.*
import org.acejump.view.Model.DEFAULT_BOUNDARY
import org.acejump.view.Model.boundaries
import org.acejump.view.Model.editor
import java.awt.event.KeyEvent

/**
 * Entry point for all actions. The IntelliJ Platform calls AceJump here.
 */

open class AceAction : DumbAwareAction() {
  val logger = Logger.getInstance(AceAction::class.java)
  override fun update(action: AnActionEvent) {
    action.presentation.isEnabled = action.getData(EDITOR) != null
  }

  override fun actionPerformed(e: AnActionEvent) {
    boundaries = DEFAULT_BOUNDARY
    editor = e.getData(EDITOR) ?: return
    val textLength = editor.document.textLength
    logger.info("Invoked on ${editor.getNameOfFileInEditor()} ($textLength)")
    Handler.activate()
  }
}

class AceTargetAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also { Handler.toggleTargetMode(true) }
}

class AceLineAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also { regexSearch(LINE_MARK) }
}

object AceNavigateAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also { Handler.toggleDefinitionMode(true) }
}

object AceKeyAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val inputEvent = e.inputEvent as? KeyEvent ?: return
    logger.info("Registered key: ${KeyEvent.getKeyText(inputEvent.keyCode)}")
    Handler.processCommand(inputEvent.keyCode)
  }
}

/**
 * Search for words in the complete file
 */

class AceWordAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also { 
		 regexSearch(ALL_WORDS, ScreenBoundary)
	 }
}

/**
 * Search for words from the start of the screen to the caret
 */

object AceWordForwardAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also { 
      regexSearch(ALL_WORDS, AfterCaretBoundary)
   }
}

/**
 * Search for words from the caret position to the start of the screen
 */

object AceWordBackwardsAction : AceAction() {
  override fun actionPerformed(e: AnActionEvent) =
    super.actionPerformed(e).also {
       regexSearch(ALL_WORDS, BeforeCaretBoundary)
    }
}

