workflow "Build and deploy on push" {
  on = "push"
  resolves = ["Build JS", "Deploy to Github Pages"]
}

action "Filter Master Branch" {
  uses = "actions/bin/filter@25b7b846d5027eac3315b50a8055ea675e2abd89"
  runs = "branch master"
}

action "Build JS" {
  uses = "actions/npm@59b64a598378f31e49cb76f27d6f3312b582f680"
  needs = ["Filter Master Branch"]
  runs = "run build"
}

action "GitHub Action for Docker" {
  uses = "actions/docker/cli@86ab5e854a74b50b7ed798a94d9b8ce175d8ba19"
  needs = ["Filter Master Branch"]
  args = "run -it --rm -v \"$PWD\":/usr/src/app -w /usr/src/app clojure clj -A:build"
}

action "Deploy to Github Pages" {
  uses = "maxheld83/ghpages@v0.2.1"
  needs = ["GitHub Action for Docker", "Build JS"]
  secrets = ["GH_PAT"]
  env = {
    BUILD_DIR = "web/public"
  }
}
