package com.template.contracts

import com.template.states.DocumentState
import com.template.states.DocumentStatus
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireThat
import net.corda.core.identity.AbstractParty

// ************
// * Contract *
// ************

class DocumentContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.DocumentContract"
    }

    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Propose -> requireThat {
                "There should be no DocumentState in the input".using(tx.inRefsOfType<DocumentState>().isEmpty())
                "There should be exactly one DocumentState in the output".using(tx.outputsOfType<DocumentState>().size == 1)

                val output = tx.outputsOfType<DocumentState>().first()

                "Proposer and Consenter must be different parties".using(output.proposer != output.consenter)
                "The initial status of the output should be PROPOSED".using(output.status == DocumentStatus.PROPOSED)
                "The version should be 0".using(output.versionNo == 0)

                val signers = tx.commands[0].signers.toSet()
                val participants = tx.outputStates[0].participants.map(AbstractParty::owningKey).toSet()

                "Only required signers may sign the transaction".using(signers == participants)
            }

            is Commands.Reject -> requireThat {
                "There should be exactly one DocumentState in the input".using(tx.inRefsOfType<DocumentState>().size == 1)
                "There should be exactly one DocumentState in the output".using(tx.outputsOfType<DocumentState>().size == 1)

                val input = tx.inputsOfType<DocumentState>().first()
                val output = tx.outputsOfType<DocumentState>().first()

                "Proposer and Consenter must be different parties".using(output.proposer != output.consenter)
                "The status of the input should be PROPOSED".using(input.status == DocumentStatus.PROPOSED)
                "The status of the output should be REJECTED".using(output.status == DocumentStatus.REJECTED)
                "The comments field should not be null or empty".using(!output.comments.isNullOrEmpty())
                "The version should be greater than 0".using(output.versionNo > 0)
                "The version should be incremented".using(output.versionNo == input.versionNo + 1)
                "Properties other than version, status, comments and updateTime should not have been changed".using(
                    output.proposer == input.proposer &&
                            output.consenter == input.consenter &&
                            output.documentTitle == input.documentTitle &&
                            output.documentType == input.documentType &&
                            output.documentJson == input.documentJson &&
                            output.linearId == input.linearId
                )
                val signers = tx.commands[0].signers.toSet()
                val participantsInOutput = tx.outputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participantsInInput = tx.inputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participants = participantsInOutput + participantsInInput

                "Only required signers may sign the transaction".using(signers == participants)
            }

            is Commands.Revise -> requireThat {
                "There should be exactly one DocumentState in the input".using(tx.inRefsOfType<DocumentState>().size == 1)
                "There should be exactly one DocumentState in the output".using(tx.outputsOfType<DocumentState>().size == 1)

                val input = tx.inputsOfType<DocumentState>().first()
                val output = tx.outputsOfType<DocumentState>().first()

                "Proposer and Consenter must be different parties".using(output.proposer != output.consenter)
                "The status of the input should be PROPOSED or REJECTED".using(input.status == DocumentStatus.PROPOSED || input.status == DocumentStatus.REJECTED)
                "The status of the output should be PROPOSED".using(output.status == DocumentStatus.PROPOSED)
                "The version should be greater than 0".using(output.versionNo > 0)
                "The version should be incremented".using(output.versionNo == input.versionNo + 1)
                "Properties other than status, documentTitle, documentType, documentJson, comments and updateTime should not have been changed".using(
                    output.proposer == input.proposer &&
                            output.consenter == input.consenter &&
                            output.linearId == input.linearId
                )
                val signers = tx.commands[0].signers.toSet()
                val participantsInOutput = tx.outputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participantsInInput = tx.inputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participants = participantsInOutput + participantsInInput

                "Only required signers may sign the transaction".using(signers == participants)
            }

            is Commands.Approve -> requireThat {
                "There should be exactly one DocumentState in the input".using(tx.inRefsOfType<DocumentState>().size == 1)
                "There should be exactly one DocumentState in the output".using(tx.outputsOfType<DocumentState>().size == 1)

                val input = tx.inputsOfType<DocumentState>().first()
                val output = tx.outputsOfType<DocumentState>().first()

                "Proposer and Consenter must be different parties".using(output.proposer != output.consenter)
                "The status of the input should be PROPOSED".using(input.status == DocumentStatus.PROPOSED)
                "The status of the output should be APPROVED".using(output.status == DocumentStatus.APPROVED)
                "The version should be greater than 0".using(output.versionNo > 0)
                "The version should be incremented".using(output.versionNo == input.versionNo + 1)
                "Properties other than status, version, comments and updateTime should not have been changed".using(
                    output.proposer == input.proposer &&
                            output.consenter == input.consenter &&
                            output.documentTitle == input.documentTitle &&
                            output.documentType == input.documentType &&
                            output.documentJson == input.documentJson &&
                            output.linearId == input.linearId
                )
                val signers = tx.commands[0].signers.toSet()
                val participantsInOutput = tx.outputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participantsInInput = tx.inputStates[0].participants.map(AbstractParty::owningKey).toSet()
                val participants = participantsInOutput + participantsInInput

                "Only required signers may sign the transaction".using(signers == participants)
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Propose : Commands
        class Approve : Commands
        class Revise : Commands
        class Reject : Commands
    }
}