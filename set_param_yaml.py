# -*- coding: utf-8 -*-
##  @package set_param.py
#   Set parameters as they have been given
#
import sys
import yaml

def set_param_yaml(filename, key, value):
    with open(filename, 'r') as f:
        doc = yaml.safe_load(f)
    doc[key] = value
    with open(filename, 'w') as f:
        yaml.dump(doc, f)

if __name__ == "__main__":
    print(f"Arguments count: {len(sys.argv)}")
    for i, arg in enumerate(sys.argv):
        print(f"Argument {i:>6}: {arg}")

    set_param_yaml(sys.argv[1], sys.argv[2], sys.argv[3])