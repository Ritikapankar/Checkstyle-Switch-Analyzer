package com.example.Checkstyle.Switch.Analyzer.checkstyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class SwitchDuplicateLogicCheck extends AbstractCheck {
    
     private int minStatementCount = 3;

     public void setMinStatementCount(int minStatementCount) {
        this.minStatementCount = minStatementCount;
     }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.LITERAL_SWITCH };
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.LITERAL_SWITCH };
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] { TokenTypes.LITERAL_SWITCH };
    }
     
    @Override
    public void visitToken(DetailAST switchNode) {
        List<DetailAST> caseGroups = extractCaseGroups(switchNode);
        List<List<DetailAST>> cleanedCases = new ArrayList<>();

        // Step 1: Har case group ke andar ke meaningful statements extract karo
        for (DetailAST caseGroup : caseGroups) {
            List<DetailAST> statements = extractMeaningfulStatements(caseGroup);
            
            // Smart Filter: Agar statements threshold se kam hain, toh ignore karo
            if (statements.size() >= minStatementCount) {
                cleanedCases.add(statements);
            }
        }

        // Step 2: Cleaned cases ko aapas mein compare karo
        for (int i = 0; i < cleanedCases.size(); i++) {
            for (int j = i + 1; j < cleanedCases.size(); j++) {
                if (areStatementListsEqual(cleanedCases.get(i), cleanedCases.get(j))) {
                    // Precision error log trigger karna
                    log(switchNode, "Duplicate logic block detected across switch branches (filtered for false positives).");
                    return; // Ek switch mein ek error kaafi hai
                }
            }
        }
    }

    private List<DetailAST> extractCaseGroups(DetailAST switchNode) {
        List<DetailAST> caseGroups = new ArrayList<>();
        DetailAST child = switchNode.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.CASE_GROUP) {
                caseGroups.add(child);
            }
            child = child.getNextSibling();
        }
        return caseGroups;
    }

    private List<DetailAST> extractMeaningfulStatements(DetailAST caseGroupNode) {
        List<DetailAST> statements = new ArrayList<>();
        DetailAST statementList = caseGroupNode.findFirstToken(TokenTypes.SLIST);
        if (statementList == null) {
            return statements;
        }

        DetailAST child = statementList.getFirstChild();
        
        while (child != null) {
            if (!isBoilerplateOrTerminal(child)) {
                statements.add(child);
            }
            child = child.getNextSibling();
        }
        return statements;
    }

    private boolean isBoilerplateOrTerminal(DetailAST node) {
        int type = node.getType();
        // Yeh wo common tokens hain jo har case ke end mein hote hain aur duplicate maan liye jate
        return type == TokenTypes.LITERAL_BREAK ||
               type == TokenTypes.LITERAL_YIELD ||
               type == TokenTypes.LITERAL_RETURN ||
               type == TokenTypes.EMPTY_STAT;
    }

    private boolean areStatementListsEqual(List<DetailAST> list1, List<DetailAST> list2) {
        if (list1.size() != list2.size()) return false;
        for (int i = 0; i < list1.size(); i++) {
            if (!areAstSubtreesEqual(list1.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean areAstSubtreesEqual(DetailAST node1, DetailAST node2) {
        if (node1 == null && node2 == null) return true;
        if (node1 == null || node2 == null) return false;

        if (node1.getType() != node2.getType() || !Objects.equals(node1.getText(), node2.getText())) {
            return false;
        }

        if (node1.getChildCount() != node2.getChildCount()) {
            return false;
        }

        DetailAST child1 = node1.getFirstChild();
        DetailAST child2 = node2.getFirstChild();
        while (child1 != null) {
            if (!areAstSubtreesEqual(child1, child2)) {
                return false;
            }
            child1 = child1.getNextSibling();
            child2 = child2.getNextSibling();
        }

        return true;
    }
    

}
