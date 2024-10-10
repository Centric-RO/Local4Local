import { render } from "@testing-library/react-native";
import GenericChip from "@/components/generic-chip/generic-chip";

describe('<GenericChip />', () => {
	it('should render properly', () => {
		const {getByText} = render(
			<GenericChip selected={false} onPress={jest.fn()} label={'chip'} />
		);

		expect(getByText('chip')).toBeTruthy();
	});
});