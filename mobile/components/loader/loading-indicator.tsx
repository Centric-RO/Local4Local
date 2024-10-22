import React from "react";
import { View, ActivityIndicator } from "react-native";
import { usePromiseTracker } from "react-promise-tracker";
import styles from "./loading-indicator-style";
import colors from "@/constants/colors";

export default function LoadingIndicator() {
  const { promiseInProgress } = usePromiseTracker();

  if (!promiseInProgress) {
    return null;
  }

  return (
    <View style={styles.loadingContainer}>
      <ActivityIndicator
        testID="loading-indicator"
        size="large"
        color={colors.THEME_500}
      />
    </View>
  );
}
