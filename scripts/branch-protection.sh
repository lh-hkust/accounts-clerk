#!/bin/bash
# Branch protection setup script for GitHub repository
# Run this script to configure main branch protection rules
#
# Prerequisites:
#   - GitHub CLI (gh) installed and authenticated
#   - Repository must exist on GitHub
#   - User must have admin permissions on the repository
#
# Usage:
#   REPO_OWNER=your-org REPO_NAME=your-repo ./branch-protection.sh

set -e

REPO_OWNER=${REPO_OWNER:-"your-org"}
REPO_NAME=${REPO_NAME:-"ai-coding"}
REPO="${REPO_OWNER}/${REPO_NAME}"

echo "Setting up branch protection for ${REPO}/main..."
echo ""

# Create branch protection rule for main branch
gh api --method PUT \
  repos/${REPO}/branches/main/protection \
  -f required_pull_request_reviews="{\"dismiss_stale_reviews\":true,\"require_code_owner_reviews\":true,\"required_approving_review_count\":1}" \
  -f required_status_checks="{\"strict\":true,\"contexts\":[\"ktlint\",\"unit-tests\",\"codeql\"]}" \
  -f enforce_admins=false \
  -f restrictions=null \
  -f required_linear_history=true \
  -f allow_force_pushes=false \
  -f allow_deletions=false \
  -f block_creations=false \
  -f required_conversation_resolution=true

echo ""
echo "Branch protection configured successfully!"
echo ""
echo "Protection rules applied:"
echo "  ✓ Require pull request reviews (1 approval)"
echo "  ✓ Dismiss stale reviews on new commits"
echo "  ✓ Require CODEOWNERS approval"
echo "  ✓ Require status checks: ktlint, unit-tests, codeql"
echo "  ✓ Require branches to be up to date before merging"
echo "  ✓ Require linear history"
echo "  ✓ Require conversation resolution"
echo "  ✓ Block force pushes"
echo "  ✓ Block deletions"
echo ""
echo "To verify: gh api repos/${REPO}/branches/main/protection"