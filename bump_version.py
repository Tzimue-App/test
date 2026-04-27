import re
import subprocess
import sys

CONFIG_FILE = "build.gradle.kts"
CHANGELOG_FILE = "CHANGELOG.md"
VERSION_PATTERN = r'version = "(.*?)"'

def run_command(cmd, abort_on_error=True):
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if result.returncode != 0 and abort_on_error:
        print(f"❌ Erreur lors de l'exécution de: {cmd}")
        print(result.stderr)
        sys.exit(1)
    return result.stdout.strip()

def get_bump_info():
    """Extracts the bump type number from the CHANGELOG.md file."""
    try:
        with open(CHANGELOG_FILE, "r", encoding="utf-8") as f:
            content = f.read()
            match = re.search(r"Bump:\s*(\d)", content)
            return int(match.group(1)) if match else None
    except FileNotFoundError:
        print(f"{CHANGELOG_FILE} not found.")
        sys.exit(1)

def calculate_new_version(current_v, bump_type, branch):
    """Calculates the new version based on SemVer, bump type, and target branch."""
    # Extract the base numbers (e.g., v0.0.0-a-SNAPSHOT -> 0.0.0)
    match = re.search(r'v?(\d+\.\d+\.\d+)', current_v)
    if not match:
        print(f"Actual version format not recognized: {current_v}")
        sys.exit(1)
        
    base_v = match.group(1)
    parts = list(map(int, base_v.split('.')))
    
    # Process Increment types 1, 2, 3
    if bump_type == 1: 
        parts[0] += 1; parts[1] = 0; parts[2] = 0
    elif bump_type == 2: 
        parts[1] += 1; parts[2] = 0
    elif bump_type == 3: 
        parts[2] += 1
    # Type 4: No numeric increment, just stage change
    
    new_base = ".".join(map(str, parts))
    
    # Suffix logic handling based on branch and bump type 4
    if bump_type == 4:
        if branch != "test":
            print(f"⚠️ Type '4' is reserved for merges to 'test'. Current branch: {branch}")
            # Fallback to dev suffix if triggered improperly
            return f"v{new_base}-a-SNAPSHOT"
        return f"v{new_base}-b-SNAPSHOT"
    
    # Types 1, 2, 3 branch-specific suffixes
    if branch == "dev":
        return f"v{new_base}-a-SNAPSHOT"
    elif branch == "test":
        return f"v{new_base}-b-SNAPSHOT"
    else:
        return f"v{new_base}" # Production/Main release format

def update_files(new_version):
    """Updates build.gradle.kts and CHANGELOG.md with the new version."""
    # 1. Update Gradle configuration
    with open(CONFIG_FILE, "r", encoding="utf-8") as f: 
        content = f.read()
    new_content = re.sub(VERSION_PATTERN, f'version = "{new_version}"', content)
    with open(CONFIG_FILE, "w", encoding="utf-8") as f: 
        f.write(new_content)
    
    # 2. Update CHANGELOG: Swap [Unreleased] to [new_version] and reset Bump field
    with open(CHANGELOG_FILE, "r", encoding="utf-8") as f: 
        content = f.read()
    new_content = content.replace("## [Unreleased]", f"## [{new_version}]")
    new_content = re.sub(r"Bump:\s*\d", "Bump: [Numéro]", new_content)
    with open(CHANGELOG_FILE, "w", encoding="utf-8") as f: 
        f.write(new_content)

def main(): 
    print("Starting Bump Version Script...")
    
    bump_type = get_bump_info()
    if not bump_type or bump_type not in [1, 2, 3, 4]:
        print("Valid 'Bump: [Numéro]' not found in CHANGELOG.md. Aborting.")
        sys.exit(1)

    branch = run_command("git rev-parse --abbrev-ref HEAD")
    
    with open(CONFIG_FILE, "r", encoding="utf-8") as f:
        current_v = re.search(VERSION_PATTERN, f.read()).group(1)
    
    new_v = calculate_new_version(current_v, bump_type, branch)
    print(f"Branch: {branch} | {current_v} -> {new_v}")
    
    update_files(new_v)
    
    # Git Workflow Execution
    print("Committing changes...")
    run_command(f"git add {CONFIG_FILE} {CHANGELOG_FILE}")
    run_command(f"git commit -m \"chore(release): {new_v} [skip ci]\"")
    
    print("Creating Git Tag...")
    run_command(f"git tag -a {new_v} -m \"Release {new_v}\"")
    
    # In GitHub Actions, pushing requires explicit origin targeting
    print("Pushing changes and tag...")
    run_command("git push origin HEAD")
    run_command(f"git push origin {new_v}")
    
    # Sync dev branch if doing a stage change merge to test
    if branch == "test" and bump_type == 4:
        print("Back-merging to dev for history synchronization...")
        run_command("git checkout dev")
        run_command("git merge test")
        run_command("git push origin dev")
        run_command("git checkout test")

    # Create GitHub Release (Requires 'gh' CLI and authentication)
    # If it fails, we only print an error, we don't crash the whole script
    print("Creating GitHub Release...")
    gh_cmd = f"gh release create {new_v} --notes-from-changelog --title \"Release {new_v}\""
    gh_result = subprocess.run(gh_cmd, shell=True, capture_output=True, text=True)
    if gh_result.returncode == 0:
        print(f"Release {new_v} created successfully on GitHub.")
    else:
        print(f"Failed to create GitHub release (Do you have 'gh' CLI and a valid token?)")
        print(gh_result.stderr)

    print("Operation completed successfully !")

if __name__ == "__main__":
    main()
