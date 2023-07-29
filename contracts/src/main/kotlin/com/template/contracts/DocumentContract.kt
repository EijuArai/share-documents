package com.template.contracts

import com.template.states.DocumentState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireThat
// ************
// * Contract *
// ************
class DocumentContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.DocumentContract"
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands>()
        val output = tx.outputsOfType<DocumentState>().first()
        when (command.value) {
            is Commands.Propose -> requireThat {
            }
            is Commands.Approve -> requireThat {
            }
            is Commands.Revise -> requireThat {
            }
            is Commands.Reject -> requireThat {
            }
            is Commands.Update -> requireThat {
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Propose : Commands
        class Update: Commands
        class Approve: Commands
        class Revise: Commands
        class Reject: Commands
    }
}